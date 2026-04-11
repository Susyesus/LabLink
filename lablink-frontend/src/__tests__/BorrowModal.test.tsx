import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BorrowModal } from '@/features/borrow/components/BorrowModal';
import { borrowApi } from '@/features/borrow/api';
import type { Equipment } from '@/features/equipment/types';
import toast from 'react-hot-toast';

// Mock the API module
jest.mock('@/features/borrow/api', () => ({
  borrowApi: { borrow: jest.fn() },
}));
jest.mock('@/core/api/apiClient', () => ({
  extractApiError: jest.fn((err: any) => err?.response?.data?.error?.message || 'Error occurred'),
}));
jest.mock('react-hot-toast', () => ({
  __esModule: true,
  default: { error: jest.fn(), success: jest.fn() }
}));

const mockEquipment: Equipment = {
  id: 'eq-001',
  name: 'Arduino Uno R4',
  description: 'WiFi-enabled microcontroller',
  serialNumber: 'SN-2024-001',
  status: 'AVAILABLE',
  category: { id: 'cat-001', name: 'Microcontrollers' },
  imageUrl: null,
};

describe('BorrowModal', () => {
  const onClose   = jest.fn();
  const onSuccess = jest.fn();

  beforeEach(() => jest.clearAllMocks());

  it('renders equipment info when open', () => {
    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    expect(screen.getByText('Arduino Uno R4')).toBeInTheDocument();
    expect(screen.getByText('Microcontrollers')).toBeInTheDocument();
  });

  it('renders nothing when equipment is null', () => {
    const { container } = render(<BorrowModal equipment={null} onClose={onClose} onSuccess={onSuccess} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('calls onClose when Cancel is clicked', () => {
    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    fireEvent.click(screen.getByText('Cancel'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('calls borrowApi.borrow on valid submission', async () => {
    (borrowApi.borrow as jest.Mock).mockResolvedValueOnce({
      data: { success: true, data: { id: 'br-001', borrowDate: '', expectedReturnDate: '', status: 'ACTIVE' } },
    });

    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    fireEvent.click(screen.getByText('Confirm Reservation'));

    await waitFor(() => expect(borrowApi.borrow).toHaveBeenCalledWith(
      expect.objectContaining({ equipmentId: 'eq-001' })
    ));
    expect(onSuccess).toHaveBeenCalled();
  });

  it('shows error toast when API returns conflict', async () => {
    (borrowApi.borrow as jest.Mock).mockRejectedValueOnce({
      isAxiosError: true,
      response: { data: { error: { message: 'Item is already unavailable' } } },
    });

    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    fireEvent.click(screen.getByText('Confirm Reservation'));

    await waitFor(() => expect(toast.error).toHaveBeenCalledWith('Item is already unavailable'));
    expect(onSuccess).not.toHaveBeenCalled();
  });
});
