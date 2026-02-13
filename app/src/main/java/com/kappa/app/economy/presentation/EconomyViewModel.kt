package com.kappa.app.economy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappa.app.core.base.ViewState
import com.kappa.app.domain.economy.CoinBalance
import com.kappa.app.domain.economy.CoinPackage
import com.kappa.app.economy.domain.usecase.GetCoinBalanceUseCase
import com.kappa.app.economy.domain.usecase.GetCoinPackagesUseCase
import com.kappa.app.economy.domain.usecase.VerifyGooglePurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Economy ViewState.
 */
data class EconomyViewState(
    val coinBalance: CoinBalance? = null,
    val packages: List<CoinPackage> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingPackages: Boolean = false,
    val isPurchasing: Boolean = false,
    val purchaseMessage: String? = null,
    val error: String? = null
) : ViewState

/**
 * Economy ViewModel.
 */
@HiltViewModel
class EconomyViewModel @Inject constructor(
    private val getCoinBalanceUseCase: GetCoinBalanceUseCase,
    private val getCoinPackagesUseCase: GetCoinPackagesUseCase,
    private val verifyGooglePurchaseUseCase: VerifyGooglePurchaseUseCase
) : ViewModel() {
    
    private val _viewState = MutableStateFlow(EconomyViewState())
    val viewState: StateFlow<EconomyViewState> = _viewState.asStateFlow()
    
    fun loadCoinBalance(userId: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, error = null)
            
            getCoinBalanceUseCase(userId)
                .onSuccess { balance ->
                    _viewState.value = _viewState.value.copy(
                        isLoading = false,
                        coinBalance = balance
                    )
                }
                .onFailure { throwable ->
                    _viewState.value = _viewState.value.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
        }
    }

    fun loadCoinPackages() {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoadingPackages = true, error = null)
            getCoinPackagesUseCase()
                .onSuccess { packages ->
                    _viewState.value = _viewState.value.copy(
                        isLoadingPackages = false,
                        packages = packages.filter { it.isActive }
                    )
                }
                .onFailure { throwable ->
                    _viewState.value = _viewState.value.copy(
                        isLoadingPackages = false,
                        error = throwable.message
                    )
                }
        }
    }

    fun verifyGooglePurchase(
        packageId: String,
        productId: String,
        purchaseToken: String,
        orderId: String?
    ) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isPurchasing = true, purchaseMessage = null, error = null)
            verifyGooglePurchaseUseCase(packageId, productId, purchaseToken, orderId)
                .onSuccess { purchase ->
                    _viewState.value = _viewState.value.copy(
                        isPurchasing = false,
                        purchaseMessage = "Purchase ${purchase.status}",
                        coinBalance = _viewState.value.coinBalance?.copy(balance = purchase.coinBalance)
                    )
                    loadCoinBalance("me")
                }
                .onFailure { throwable ->
                    _viewState.value = _viewState.value.copy(
                        isPurchasing = false,
                        error = throwable.message
                    )
                }
        }
    }
}
