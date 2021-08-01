package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.facebook.FacebookSdk.getCacheDir
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RootGraphDirections
import com.wafflestudio.snutt2.databinding.FragmentTimetableBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.android.defaultNavOptions
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.HomeFragmentDirections
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class TimetableFragment : BaseFragment() {

    private lateinit var binding: FragmentTimetableBinding

    @Inject
    lateinit var dialogController: DialogController

    @Inject
    lateinit var apiOnError: ApiOnError

    private val selectedTimetableViewModel: SelectedTimetableViewModel by activityViewModels()
    private val tableListViewModel: TableListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimetableBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Observables.combineLatest(
            selectedTimetableViewModel.lastViewedTable.asObservable().filterEmpty(),
            selectedTimetableViewModel.selectedPreviewTheme.asObservable()
        )
            .distinctUntilChanged()
            .bindUi(this) { (table, previewTheme) ->
                binding.timetable.lectures = table.lectureList
                binding.timetable.theme = previewTheme.get() ?: table.theme
                binding.appBarTitle.text = table.title
                val creditText = getString(
                    R.string.timetable_credit,
                    table.lectureList.fold(0L) { acc, lecture -> acc + lecture.credit }
                )
                binding.creditText.text = creditText
            }

        selectedTimetableViewModel.trimParam.asObservable()
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.trimParam = it
            }

        binding.timetable.setOnLectureClickListener {
            routeLectureDetail(it)
        }

        binding.drawerButton.throttledClicks()
            .bindUi(this) {
                (requireParentFragment().view as? DrawerLayout)?.open()
            }

        binding.lectureListButton.throttledClicks()
            .bindUi(this) {
                routeLectureList()
            }

        binding.shareButton.throttledClicks()
            .bindUi(this) {
                getScreenShotFromView(binding.timetable)
                    .subscribeOn(Schedulers.io())
                    .flatMap { saveImage(it) }
                    .bindUi(this@TimetableFragment, onError = {
                        Timber.e(it)
                        requireContext().toast("시간표 공유에 실패하였습니다.")
                    }, onSuccess = {
                        shareTimetable(it)
                    })

            }

        binding.notificationsButton.throttledClicks()
            .bindUi(this) {
                routeNotifications()
            }

        binding.appBarTitle.throttledClicks()
            .bindUi(this) {
                dialogController.showTextDialog(
                    R.string.home_drawer_change_name_dialog_title,
                    selectedTimetableViewModel.lastViewedTable.get().value?.title,
                    R.string.home_drawer_change_name_dialog_hint
                )
                    .flatMapCompletable {
                        tableListViewModel.changeNameTable(
                            selectedTimetableViewModel.lastViewedTable.get().value?.id!!,
                            it
                        )
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onError = apiOnError)
            }

    }

    private fun routeLectureDetail(lecture: LectureDto) {
        val action =
            if (lecture.isCustom) RootGraphDirections.actionGlobalCustomLectureDetailFragment(
                lecture
            )
            else RootGraphDirections.actionGlobalLectureDetailFragment(
                lecture
            )
        findNavController().navigate(action, defaultNavOptions)
    }

    private fun routeNotifications() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToNotificationsFragment(), defaultNavOptions
        )
    }

    private fun routeLectureList() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToTableLecturesFragment(), defaultNavOptions
        )
    }

    private fun getScreenShotFromView(v: View): Single<Bitmap> {
        return Single.fromCallable {
            val screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
            return@fromCallable screenshot
        }
    }

    private fun saveImage(image: Bitmap): Single<Uri> {
        return Single.fromCallable {
            val imagesFolder = File(getCacheDir(), "images")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            return@fromCallable FileProvider.getUriForFile(
                requireContext(),
                "com.wafflestudio.snutt2.fileprovider",
                file
            )
        }
    }

    private fun shareTimetable(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
        }
        startActivity(createChooser(shareIntent, "공유하기"))
    }
}
