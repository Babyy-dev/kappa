package com.kappa.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappa.app.core.network.ApiService
import com.kappa.app.core.network.model.AnnouncementDto
import com.kappa.app.core.network.model.CoinPackageDto
import com.kappa.app.core.network.model.CoinTransactionDto
import com.kappa.app.core.network.model.RewardRequestCreateDto
import com.kappa.app.core.network.model.RewardRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyMenuState(
    val coinBalance: Long = 0L,
    val diamondBalance: Long = 0L,
    val lockedDiamonds: Long = 0L,
    val packages: List<CoinPackageDto> = emptyList(),
    val transactions: List<CoinTransactionDto> = emptyList(),
    val rewards: List<RewardRequestDto> = emptyList(),
    val announcements: List<AnnouncementDto> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

@HiltViewModel
class MyMenuViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _viewState = MutableStateFlow(MyMenuState())
    val viewState: StateFlow<MyMenuState> = _viewState.asStateFlow()

    fun loadRecharge() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null, message = null) }
            val coinBalance = runCatching { apiService.getCoinBalance() }.getOrNull()
            val packages = runCatching { apiService.getCoinPackages() }.getOrNull()
            _viewState.update {
                it.copy(
                    coinBalance = coinBalance?.data?.balance ?: it.coinBalance,
                    packages = packages?.data.orEmpty(),
                    isLoading = false,
                    error = coinBalance?.takeIf { !it.success }?.error
                        ?: packages?.takeIf { !it.success }?.error
                )
            }
        }
    }

    fun loadBackpack() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null, message = null) }
            val tx = runCatching { apiService.getCoinTransactions(50) }.getOrNull()
            _viewState.update {
                it.copy(
                    transactions = tx?.data.orEmpty(),
                    isLoading = false,
                    error = tx?.takeIf { !it.success }?.error
                )
            }
        }
    }

    fun loadVip() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null, message = null) }
            val diamonds = runCatching { apiService.getDiamondBalance() }.getOrNull()
            val coins = runCatching { apiService.getCoinBalance() }.getOrNull()
            _viewState.update {
                it.copy(
                    coinBalance = coins?.data?.balance ?: it.coinBalance,
                    diamondBalance = diamonds?.data?.balance ?: it.diamondBalance,
                    lockedDiamonds = diamonds?.data?.locked ?: it.lockedDiamonds,
                    isLoading = false,
                    error = diamonds?.takeIf { !it.success }?.error
                        ?: coins?.takeIf { !it.success }?.error
                )
            }
        }
    }

    fun loadStarPath() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null, message = null) }
            val rewards = runCatching { apiService.getRewardRequests() }.getOrNull()
            val diamonds = runCatching { apiService.getDiamondBalance() }.getOrNull()
            _viewState.update {
                it.copy(
                    rewards = rewards?.data.orEmpty(),
                    diamondBalance = diamonds?.data?.balance ?: it.diamondBalance,
                    lockedDiamonds = diamonds?.data?.locked ?: it.lockedDiamonds,
                    isLoading = false,
                    error = rewards?.takeIf { !it.success }?.error
                        ?: diamonds?.takeIf { !it.success }?.error
                )
            }
        }
    }

    fun loadRoomRewards() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null, message = null) }
            val announcements = runCatching { apiService.getAnnouncements() }.getOrNull()
            _viewState.update {
                it.copy(
                    announcements = announcements?.data.orEmpty().filter { item -> item.isActive },
                    isLoading = false,
                    error = announcements?.takeIf { !it.success }?.error
                )
            }
        }
    }

    fun requestReward(diamonds: Long) {
        if (diamonds <= 0) {
            _viewState.update { it.copy(error = "Diamonds must be greater than 0") }
            return
        }
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null, message = null) }
            val response = runCatching {
                apiService.createRewardRequest(RewardRequestCreateDto(diamonds))
            }.getOrNull()
            if (response == null) {
                _viewState.update { it.copy(isLoading = false, error = "Unable to request reward") }
                return@launch
            }
            if (!response.success) {
                _viewState.update { it.copy(isLoading = false, error = response.error ?: "Unable to request reward") }
                return@launch
            }
            _viewState.update {
                it.copy(
                    message = "Reward request submitted",
                    isLoading = false
                )
            }
            loadStarPath()
        }
    }

    fun clearMessage() {
        _viewState.update { it.copy(message = null, error = null) }
    }
}
