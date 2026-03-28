package com.lablink.android.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lablink.android.R
import com.lablink.android.data.api.RetrofitClient
import com.lablink.android.data.local.SessionManager
import com.lablink.android.data.model.*
import com.lablink.android.databinding.ActivityDashboardBinding
import com.lablink.android.ui.auth.LoginActivity
import com.lablink.android.ui.profile.ProfileActivity
import com.lablink.android.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var equipmentAdapter: EquipmentAdapter

    private var allCategories: List<CategoryDto> = emptyList()
    private var selectedCategoryId: String? = null
    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        setupUI()
        setupListeners()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh user name in case profile was updated
        binding.tvUserName.text = sessionManager.getUserName() ?: "Student"
    }

    private fun setupUI() {
        // Welcome text
        binding.tvUserName.text = sessionManager.getUserName() ?: "Student"

        // RecyclerView
        equipmentAdapter = EquipmentAdapter { equipment ->
            // Show equipment details in a dialog
            showEquipmentDetail(equipment)
        }
        binding.rvEquipment.apply {
            layoutManager = GridLayoutManager(this@DashboardActivity, 2)
            adapter = equipmentAdapter
        }
    }

    private fun setupListeners() {
        // Profile button
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Search with debounce
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            private var searchRunnable: Runnable? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { binding.etSearch.removeCallbacks(it) }
                searchRunnable = Runnable {
                    searchQuery = s.toString().trim().ifEmpty { null }
                    loadEquipment()
                }
                binding.etSearch.postDelayed(searchRunnable!!, 400)
            }
        })

        // "All" chip
        binding.chipAll.setOnClickListener {
            selectedCategoryId = null
            loadEquipment()
        }
    }

    private fun loadData() {
        loadCategories()
        loadEquipment()
    }

    private fun loadCategories() {
        RetrofitClient.instance.getCategories().enqueue(object : Callback<ApiResponse<CategoriesWrapper>> {
            override fun onResponse(
                call: Call<ApiResponse<CategoriesWrapper>>,
                response: Response<ApiResponse<CategoriesWrapper>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val categories = response.body()!!.data?.categories ?: emptyList()
                    allCategories = categories
                    populateCategoryChips(categories)
                }
            }

            override fun onFailure(call: Call<ApiResponse<CategoriesWrapper>>, t: Throwable) {
                // Categories are non-critical, silently fail
            }
        })
    }

    private fun populateCategoryChips(categories: List<CategoryDto>) {
        // Clear existing chips except "All"
        val chipGroup = binding.chipGroupCategories
        while (chipGroup.childCount > 1) {
            chipGroup.removeViewAt(1)
        }

        for (category in categories) {
            val chip = Chip(this).apply {
                text = category.name
                isCheckable = true
                setOnClickListener {
                    selectedCategoryId = category.id
                    loadEquipment()
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun loadEquipment() {
        binding.progressEquipment.show()
        binding.emptyState.hide()

        RetrofitClient.instance.getEquipment(
            search = searchQuery,
            categoryId = selectedCategoryId,
            page = 1,
            limit = 50
        ).enqueue(object : Callback<ApiResponse<EquipmentPageResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<EquipmentPageResponse>>,
                response: Response<ApiResponse<EquipmentPageResponse>>
            ) {
                binding.progressEquipment.hide()

                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()!!.data?.equipment ?: emptyList()
                    equipmentAdapter.submitList(items)

                    if (items.isEmpty()) {
                        binding.emptyState.show()
                        binding.rvEquipment.hide()
                    } else {
                        binding.emptyState.hide()
                        binding.rvEquipment.show()
                    }
                } else if (response.code() == 401) {
                    handleUnauthorized()
                } else {
                    binding.root.snackbarError(parseErrorMessage(response))
                }
            }

            override fun onFailure(call: Call<ApiResponse<EquipmentPageResponse>>, t: Throwable) {
                binding.progressEquipment.hide()
                if (!NetworkUtils.isConnected(this@DashboardActivity)) {
                    binding.root.snackbarError(getString(R.string.error_no_internet))
                } else {
                    binding.root.snackbarError(getString(R.string.error_network))
                }
            }
        })
    }

    private fun showEquipmentDetail(equipment: EquipmentDto) {
        val statusText = when (equipment.status) {
            "AVAILABLE" -> "✅ Available"
            "UNAVAILABLE" -> "❌ Unavailable"
            "MAINTENANCE" -> "🔧 Under Maintenance"
            else -> equipment.status
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(equipment.name)
            .setMessage(
                "${equipment.description ?: "No description"}\n\n" +
                "Category: ${equipment.category?.name ?: "N/A"}\n" +
                "Serial: ${equipment.serialNumber ?: "N/A"}\n" +
                "Status: $statusText"
            )
            .setPositiveButton("Close", null)
            .show()
    }

    private fun handleUnauthorized() {
        sessionManager.clearSession()
        toast(getString(R.string.error_session_expired))
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
