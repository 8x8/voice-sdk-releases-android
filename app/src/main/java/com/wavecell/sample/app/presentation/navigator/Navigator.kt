package com.wavecell.sample.app.presentation.navigator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.eght.voice.sdk.model.VoiceCallAudioOption
import com.wavecell.sample.app.BuildConfig
import com.wavecell.sample.app.R
import com.wavecell.sample.app.WavecellApplication
import com.wavecell.sample.app.constants.Constants.PHONE_NUMBER_HINT
import com.wavecell.sample.app.custom.bottomsheets.AudioOptionsBottomSheet
import com.wavecell.sample.app.custom.bottomsheets.PlaceCallBottomSheet
import com.wavecell.sample.app.custom.dialog.DialogInput
import com.wavecell.sample.app.custom.dialog.DialogSingleChoice
import com.wavecell.sample.app.presentation.model.ActivityMainViewModel
import com.wavecell.sample.app.presentation.notifications.CallService
import com.wavecell.sample.app.presentation.view.activity.ActivityCall
import com.wavecell.sample.app.presentation.view.activity.ActivityRegister
import java.io.File
import java.util.*

class Navigator(private val activity: AppCompatActivity) {

    fun startCallActivity() {
        val intent = Intent(activity, ActivityCall::class.java)
        activity.startActivity(intent)
    }

    fun startRegistrationActivity() {
        val intent = Intent(activity, ActivityRegister::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    @JvmOverloads
    fun showPlaceCallBottomSheet(viewModel: ActivityMainViewModel, userId: String = "") {
        val fragmentTag = PlaceCallBottomSheet.NAME
        val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
        (fragment as? PlaceCallBottomSheet)?.dismiss()
        val bottomSheet = PlaceCallBottomSheet.newInstance(activity.supportFragmentManager, userId)
        bottomSheet.setOnBottomSheetStateListener(viewModel)
        bottomSheet.expand()
    }

    fun showUserNameDialog(name: String, listener: DialogInput.ClickListener) {
        val tag = DialogInput.TAG
        val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
        (fragment as? DialogInput)?.dismiss()
        val title = activity.getString(R.string.update_user_name)
        val hint = activity.getString(R.string.default_name)
        val dialog = DialogInput(DialogInput.Option.NAME, title, hint, name, listener)
        dialog.show(activity.supportFragmentManager, tag)
    }

    fun showPhoneNumberDialog(phoneNumber: String, listener: DialogInput.ClickListener) {
        val tag = DialogInput.TAG
        val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
        (fragment as? DialogInput)?.dismiss()
        val title = activity.getString(R.string.update_phone_number)
        val dialog = DialogInput(DialogInput.Option.PHONE_NUMBER, title, PHONE_NUMBER_HINT, phoneNumber, listener)
        dialog.show(activity.supportFragmentManager, tag)
    }

    fun showRingtoneDialog(items: Array<String>, index: Int, listener: DialogSingleChoice.ClickListener) {
        val tag = DialogSingleChoice.TAG
        val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
        (fragment as? DialogSingleChoice)?.dismiss()
        val title = activity.getString(R.string.update_ringtone)
        val dialog = DialogSingleChoice(title, items, index, listener)
        dialog.show(activity.supportFragmentManager, tag)
    }

    fun showInboundCallPathDialog(items: Array<String>, index: Int, listener: DialogSingleChoice.ClickListener) {
        val tag = DialogSingleChoice.TAG
        val fragment = activity.supportFragmentManager.findFragmentByTag(tag)
        (fragment as? DialogSingleChoice)?.dismiss()
        val title = activity.getString(R.string.update_inbound_call_path)
        val dialog = DialogSingleChoice(title, items, index, listener)
        dialog.show(activity.supportFragmentManager, tag)
    }

    fun showAudioOptionBottomSheet(option: VoiceCallAudioOption) {
        val fragmentTag = AudioOptionsBottomSheet.NAME
        val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
        (fragment as? AudioOptionsBottomSheet)?.dismiss()
        val bottomSheet = AudioOptionsBottomSheet.newInstance(activity.supportFragmentManager, option)
        bottomSheet.expand()
    }

    fun presentCallUIAndNotification(uuid: UUID, calleeId: String) {
        /* Show incoming call screen which can handle the ACTIVITY_LAUNCH_ACTION implicit action */
        var intent = Intent(WavecellApplication.IntentAction.ONGOING_ACTIVITY_LAUNCH_ACTION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)

        /* Call Service to show the dial up notification with Hangup action*/
        intent = Intent(activity.applicationContext, CallService::class.java)
        intent.action = WavecellApplication.IntentAction.PRESENT_DIALING_CALL_ACTION
        intent.putExtra(WavecellApplication.IntentExtra.CALLEE_ID, calleeId)
        val bundle = Bundle()
        bundle.putSerializable(WavecellApplication.IntentExtra.CALL_UNIQUE_ID, uuid)
        intent.putExtras(bundle)
        activity.applicationContext.startForegroundService(intent)
    }

    fun startShareFileAction(file: File) {
        val authority = StringBuilder(BuildConfig.APPLICATION_ID).append(".provider").toString()
        val fileUri = FileProvider.getUriForFile(activity, authority, file)
        val fileIntent = Intent(Intent.ACTION_SEND)
        fileIntent.type = "*/*"
        fileIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        activity.startActivity(fileIntent)
    }
}