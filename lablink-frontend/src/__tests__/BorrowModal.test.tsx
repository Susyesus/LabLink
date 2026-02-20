import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BorrowModal } from '@/components/borrow/BorrowModal';
import { borrowApi } from '@/services/api';
import type { Equipment } from '@/types';

// Mock the API module
jest.mock('@/services/api', () => ({
  borrowApi: { borrow: jest.fn() },
}));
jest.mock('react-hot-toast', () => ({ error: jest.fn(), success: jest.fn() }));

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

  it('calls onClose when Cancel is clicked', async () => {
    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    await userEvent.click(screen.getByText('Cancel'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('calls borrowApi.borrow on valid submission', async () => {
    (borrowApi.borrow as jest.Mock).mockResolvedValueOnce({
      data: { success: true, data: { id: 'br-001', borrowDate: '', expectedReturnDate: '', status: 'ACTIVE' } },
    });

    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    await userEvent.click(screen.getByText('Confirm Reservation'));

    await waitFor(() => expect(borrowApi.borrow).toHaveBeenCalledWith(
      expect.objectContaining({ equipmentId: 'eq-001' })
    ));
    expect(onSuccess).toHaveBeenCalled();
  });

  it('shows error toast when API returns conflict', async () => {
    const toast = require('react-hot-toast');
    (borrowApi.borrow as jest.Mock).mockRejectedValueOnce({
      isAxiosError: true,
      response: { data: { error: { message: 'Item is already unavailable' } } },
    });

    render(<BorrowModal equipment={mockEquipment} onClose={onClose} onSuccess={onSuccess} />);
    await userEvent.click(screen.getByText('Confirm Reservation'));

    await waitFor(() => expect(toast.error).toHaveBeenCalledWith('Item is already unavailable'));
    expect(onSuccess).not.toHaveBeenCalled();
  });
});
