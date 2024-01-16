package com.clementcorporation.levosonusii.domain.use_cases

import com.google.firebase.auth.FirebaseAuth

object SignOutUseCase {
    operator fun invoke(navigate: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        navigate()
    }
}