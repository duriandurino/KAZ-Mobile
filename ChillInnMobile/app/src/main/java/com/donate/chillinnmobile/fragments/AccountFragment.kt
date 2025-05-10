package com.donate.chillinnmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.donate.chillinnmobile.R
import com.donate.chillinnmobile.adapters.PaymentMethodAdapter
import com.donate.chillinnmobile.model.PaymentMethodType
import com.donate.chillinnmobile.utils.showToast
import com.donate.chillinnmobile.viewmodel.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

/**
 * Fragment for managing user account settings
 */
class AccountFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    
    // UI components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var changePasswordButton: Button
    private lateinit var biometricSwitch: SwitchMaterial
    private lateinit var paymentMethodsRecyclerView: RecyclerView
    private lateinit var noPaymentMethodsText: View
    private lateinit var addPaymentMethodButton: Button
    private lateinit var saveChangesButton: Button
    private lateinit var progressBar: ProgressBar
    
    // Adapter
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        
        // Initialize ViewModel
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        
        // Initialize UI components
        initializeViews(view)
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up observers
        setupObservers()
        
        // Set up click listeners
        setupClickListeners()
        
        // Load data
        loadData()
        
        return view
    }
    
    private fun initializeViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        changePasswordButton = view.findViewById(R.id.changePasswordButton)
        biometricSwitch = view.findViewById(R.id.biometricSwitch)
        paymentMethodsRecyclerView = view.findViewById(R.id.paymentMethodsRecyclerView)
        noPaymentMethodsText = view.findViewById(R.id.noPaymentMethodsText)
        addPaymentMethodButton = view.findViewById(R.id.addPaymentMethodButton)
        saveChangesButton = view.findViewById(R.id.saveChangesButton)
        progressBar = view.findViewById(R.id.progressBar)
        
        // Set up toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupRecyclerView() {
        paymentMethodAdapter = PaymentMethodAdapter(
            onRemoveClick = { paymentMethod ->
                confirmRemovePaymentMethod(paymentMethod.id)
            }
        )
        
        paymentMethodsRecyclerView.adapter = paymentMethodAdapter
    }
    
    private fun setupObservers() {
        // Observe user data changes
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                nameEditText.setText(it.name)
                emailEditText.setText(it.email)
                phoneEditText.setText(it.phone)
            }
        }
        
        // Observe payment methods
        userViewModel.paymentMethods.observe(viewLifecycleOwner) { paymentMethods ->
            paymentMethodAdapter.submitList(paymentMethods)
            
            // Show or hide the "no payment methods" text
            if (paymentMethods.isNullOrEmpty()) {
                noPaymentMethodsText.visibility = View.VISIBLE
                paymentMethodsRecyclerView.visibility = View.GONE
            } else {
                noPaymentMethodsText.visibility = View.GONE
                paymentMethodsRecyclerView.visibility = View.VISIBLE
            }
        }
        
        // Observe loading state
        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe error messages
        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showToast(it)
                userViewModel.error.value = null // Clear the error
            }
        }
    }
    
    private fun setupClickListeners() {
        // Change password
        changePasswordButton.setOnClickListener {
            // Would navigate to change password screen in a real app
            showToast("Change password functionality would open a dialog or navigate to a new screen")
        }
        
        // Biometric login toggle
        biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            // In a real app, this would enable/disable biometric authentication
            showToast("Biometric login ${if (isChecked) "enabled" else "disabled"}")
        }
        
        // Add payment method
        addPaymentMethodButton.setOnClickListener {
            showAddPaymentMethodDialog()
        }
        
        // Save changes
        saveChangesButton.setOnClickListener {
            saveUserProfile()
        }
    }
    
    private fun loadData() {
        userViewModel.getCurrentUser()
        userViewModel.getUserPaymentMethods()
    }
    
    private fun saveUserProfile() {
        val name = nameEditText.text.toString()
        val phone = phoneEditText.text.toString()
        
        if (name.isBlank()) {
            showToast("Please enter your name")
            return
        }
        
        if (phone.isBlank()) {
            showToast("Please enter your phone number")
            return
        }
        
        // Show loading
        progressBar.visibility = View.VISIBLE
        
        // Update profile
        userViewModel.updateUserProfile(
            fullName = name,
            phoneNumber = phone
        )
        
        showToast("Profile updated successfully")
        progressBar.visibility = View.GONE
    }
    
    private fun showAddPaymentMethodDialog() {
        // In a real app, this would open a dialog or navigate to a screen for adding payment methods
        // For simplicity, we'll just add a mock card
        addMockPaymentMethod()
    }
    
    private fun addMockPaymentMethod() {
        userViewModel.addPaymentMethod(
            type = PaymentMethodType.VISA,
            cardNumber = "4111111111111111",
            cardholderName = nameEditText.text.toString().ifBlank { "Card Owner" },
            expiryMonth = 12,
            expiryYear = 2025,
            setAsDefault = userViewModel.paymentMethods.value.isNullOrEmpty()
        )
        
        showToast("Payment method added successfully")
    }
    
    private fun confirmRemovePaymentMethod(paymentMethodId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Payment Method")
            .setMessage("Are you sure you want to remove this payment method?")
            .setPositiveButton("Remove") { _, _ ->
                userViewModel.removePaymentMethod(paymentMethodId)
                showToast("Payment method removed")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
} 