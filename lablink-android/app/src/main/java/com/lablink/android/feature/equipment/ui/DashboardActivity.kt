package com.lablink.android.feature.equipment.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lablink.android.R
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.core.util.*
import com.lablink.android.databinding.ActivityDashboardBinding
import com.lablink.android.feature.auth.ui.LoginActivity
import com.lablink.android.feature.equipment.contract.DashboardContract
import com.lablink.android.feature.equipment.data.EquipmentRepository
import com.lablink.android.feature.equipment.model.CategoryDto
import com.lablink.android.feature.equipment.model.EquipmentDto
import com.lablink.android.feature.equipment.presenter.DashboardPresenter
import com.lablink.android.feature.profile.ui.ProfileActivity

/**
 * Dashboard screen — thin MVP View implementation.
 * All business logic is delegated to [DashboardPresenter].
 */
class DashboardActivity : AppCompatActivity(), DashboardContract.View {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var presenter: DashboardPresenter
    private lateinit var equipmentAdapter: EquipmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        RetrofitClient.init(sessionManager)

        val repository = EquipmentRepository(RetrofitClient.instance)
        presenter = DashboardPresenter(repository, sessionManager, this)
        presenter.attachView(this)

        setupUI()
        setupListeners()
        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh user name in case profile was updated
        binding.tvUserName.text = presenter.getUserName()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupUI() {
        // Welcome text
        binding.tvUserName.text = presenter.getUserName()

        // RecyclerView
        equipmentAdapter = EquipmentAdapter { equipment ->
            presenter.onEquipmentClicked(equipment)
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
                    val query = s.toString().trim().ifEmpty { null }
                    presenter.onSearchQueryChanged(query)
                }
                binding.etSearch.postDelayed(searchRunnable!!, 400)
            }
        })

        // "All" chip
        binding.chipAll.setOnClickListener {
            presenter.onCategorySelected(null)
        }
    }

    // ─── DashboardContract.View Implementation ─────────────────

    override fun showLoading(show: Boolean) {
        if (show) binding.progressEquipment.show() else binding.progressEquipment.hide()
        if (show) binding.emptyState.hide()
    }

    override fun showError(message: String) {
        binding.root.snackbarError(message)
    }

    override fun showNetworkError() {
        binding.root.snackbarError(getString(R.string.error_no_internet))
    }

    override fun handleUnauthorized() {
        sessionManager.clearSession()
        toast(getString(R.string.error_session_expired))
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    override fun showEquipment(items: List<EquipmentDto>) {
        equipmentAdapter.submitList(items)
        if (items.isNotEmpty()) {
            binding.rvEquipment.show()
        }
    }

    override fun showEmptyState(show: Boolean) {
        if (show) {
            binding.emptyState.show()
            binding.rvEquipment.hide()
        } else {
            binding.emptyState.hide()
            binding.rvEquipment.show()
        }
    }

    override fun showCategories(categories: List<CategoryDto>) {
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
                    presenter.onCategorySelected(category.id)
                }
            }
            chipGroup.addView(chip)
        }
    }

    override fun showEquipmentDetail(equipment: EquipmentDto) {
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

    override fun setUserName(name: String) {
        binding.tvUserName.text = name
    }
}
