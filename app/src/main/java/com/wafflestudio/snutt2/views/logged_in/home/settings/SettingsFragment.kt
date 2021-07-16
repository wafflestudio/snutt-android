package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentSettingsBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import de.psdev.licensesdialog.LicensesDialog
import java.lang.Exception


@AndroidEntryPoint
class SettingsFragment : BaseFragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userSettingButton.throttledClicks()
            .bindUi(this) {
                routeUserSettings()
            }

        binding.userSettingButton.throttledClicks()
            .bindUi(this) {
                routeTimetableSettings()
            }

        binding.versionInfo.text = try {
            requireContext().packageManager.getPackageInfo(
                requireContext().applicationContext.packageName, 0
            ).versionName
        } catch (e: Exception) {
            "-"
        }

        binding.teamInfoButton.throttledClicks()
            .bindUi(this) {
                routeTeamInfo()
            }

        binding.tipOffButton.throttledClicks()
            .bindUi(this) {
                // TODO
            }

        binding.licenseInfoButton.throttledClicks()
            .bindUi(this) {
                showLicenseDialog()
            }

        binding.serviceInfoButton.throttledClicks()
            .bindUi(this) {
                // TODO
            }

        binding.privacyInfoButton.throttledClicks()
            .bindUi(this) {
                // TODO
            }
    }

    private fun routeUserSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_userSettingsFragment)
    }

    private fun routeTimetableSettings() {
        findNavController().navigate(R.id.action_homeFragment_to_timetableSettingsFragment)
    }

    private fun routeTeamInfo() {
        findNavController().navigate(R.id.action_homeFragment_to_teamInfoFragment)
    }

    private fun showLicenseDialog() {
        LicensesDialog.Builder(context)
            .setNotices(R.raw.notices)
            .setIncludeOwnLicense(true)
            .build()
            .show()
    }
}
