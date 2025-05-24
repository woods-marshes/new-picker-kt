package io.github.woods_marshes.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import io.github.woods_marshes.base.builder.PickerKtConfiguration
import io.github.woods_marshes.ui.MainActivity

class PickerKtActivityResult : ActivityResultContract<PickerKtConfiguration, List<Uri>>() {
    companion object {
        const val RESULT_CONTRACT_KEY_PICKER_CONFIG = "PickerKtConfiguration"
        const val RESULT_CONTRACT_KEY_RESULT_URL_LIST_CONFIG = "ResultUrlList"
    }

    override fun createIntent(
        context: Context,
        input: PickerKtConfiguration,
    ): Intent {
        return Intent(context, MainActivity::class.java).apply {
            putExtra(RESULT_CONTRACT_KEY_PICKER_CONFIG, input)
        }
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): List<Uri> {
        val resultUriList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableArrayExtra(RESULT_CONTRACT_KEY_RESULT_URL_LIST_CONFIG, Uri::class.java)?.toList()?.filterNotNull()
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableArrayExtra(RESULT_CONTRACT_KEY_RESULT_URL_LIST_CONFIG)?.toList()?.mapNotNull { it as? Uri }
        }
        if (resultCode != Activity.RESULT_OK || resultUriList == null || resultUriList.isEmpty()) {
            return emptyList()
        }
        return resultUriList
    }
}