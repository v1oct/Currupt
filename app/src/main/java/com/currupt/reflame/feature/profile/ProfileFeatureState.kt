package com.currupt.reflame.feature.profile

import com.currupt.reflame.account.model.UserEntitlement

data class ProfileFeatureState(
    val entitlement: UserEntitlement = UserEntitlement.FREE,
    val isEditing: Boolean = false
)
