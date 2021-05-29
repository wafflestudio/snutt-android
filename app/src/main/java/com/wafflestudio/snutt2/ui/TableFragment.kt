package com.wafflestudio.snutt2.ui

import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.LectureManager.OnLectureChangedListener
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.view.TableView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 16..
 */
@AndroidEntryPoint
class TableFragment : SNUTTBaseFragment(), OnLectureChangedListener {

    @Inject
    lateinit var lectureManager: LectureManager

    @Inject
    lateinit var prefStorage: PrefStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_table, container, false)

        mInstance = rootView.findViewById(R.id.timetable)

        setHasOptionsMenu(true)
        return rootView
    }

    private fun showCaptureAlert() {
        val alert = AlertDialog.Builder(requireActivity())
        alert.setMessage("시간표를 이미지로 저장하시겠습니까?")
        alert.setPositiveButton(
            "확인",
            DialogInterface.OnClickListener { dialog, whichButton ->
                val bitmap = getScreenShotFromView(mInstance!!)
                if (bitmap != null) {
                    saveMediaToStorage(bitmap)
                } else {
                    Toast.makeText(requireContext(), "이미지 저장을 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        ).setNegativeButton("취소") { dialog, whichButton -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }

    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // getting the contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            requireContext().contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext(), "시간표 이미지를 저장하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_table, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_more) {
            mainActivity!!.startTableList()
            return true
        }
        if (id == R.id.capture) {
            showCaptureAlert()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        lectureManager.removeListener(this)
    }

    override fun onResume() {
        super.onResume()
        lectureManager.addListener(this)
    }

    override fun notifyLecturesChanged() {
        mInstance!!.invalidate()
    }

    override fun notifySearchedLecturesChanged() {}

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private var mInstance: TableView? = null

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): TableFragment {
            val fragment = TableFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
