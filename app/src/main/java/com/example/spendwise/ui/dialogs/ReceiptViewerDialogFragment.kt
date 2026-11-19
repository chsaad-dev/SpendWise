package com.example.spendwise.ui.dialogs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import com.example.spendwise.R
import java.io.File

class ReceiptViewerDialogFragment : DialogFragment() {

    private var receiptPath: String? = null
    private lateinit var ivFullReceipt: ImageView
    private val matrix = Matrix()
    private var baseScale = 1.0f
    private var currentScale = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_SpendWise_FullScreenDialog)
        receiptPath = arguments?.getString(ARG_RECEIPT_PATH)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#090A0D")))
            WindowCompat.setDecorFitsSystemWindows(this, false)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
            WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_receipt_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivFullReceipt = view.findViewById(R.id.iv_full_receipt)
        val btnClose = view.findViewById<ImageButton>(R.id.btn_close_viewer)
        val topBar = view.findViewById<View>(R.id.top_overlay_bar)

        btnClose.setOnClickListener {
            dismiss()
        }

        // Status-bar insets padding
        ViewCompat.setOnApplyWindowInsetsListener(topBar) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val basePaddingTop = (12 * resources.displayMetrics.density).toInt()
            v.updatePadding(top = statusBar.top + basePaddingTop)
            insets
        }

        val path = receiptPath
        if (path.isNullOrBlank()) {
            Toast.makeText(context, "Receipt not available", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        val bitmap = loadReceiptBitmap(path)
        if (bitmap != null) {
            ivFullReceipt.setImageBitmap(bitmap)
            ivFullReceipt.post {
                resetToFitCenter()
                setupZoomAndPan(ivFullReceipt)
            }
        } else {
            Toast.makeText(context, "Unable to load receipt image", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun loadReceiptBitmap(path: String): Bitmap? {
        return try {
            val file = File(path)
            if (file.exists()) {
                val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(path, boundsOpts)

                var sample = 1
                while (boundsOpts.outWidth / sample > 4096 || boundsOpts.outHeight / sample > 4096) {
                    sample *= 2
                }

                val decodeOpts = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                BitmapFactory.decodeFile(path, decodeOpts)
            } else if (path.startsWith("content://")) {
                val uri = Uri.parse(path)
                requireContext().contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun resetToFitCenter() {
        val d = ivFullReceipt.drawable ?: return
        val dWidth = d.intrinsicWidth.toFloat()
        val dHeight = d.intrinsicHeight.toFloat()
        val vWidth = ivFullReceipt.width.toFloat()
        val vHeight = ivFullReceipt.height.toFloat()
        if (dWidth <= 0 || dHeight <= 0 || vWidth <= 0 || vHeight <= 0) return

        baseScale = minOf(vWidth / dWidth, vHeight / dHeight)
        val dx = (vWidth - dWidth * baseScale) / 2f
        val dy = (vHeight - dHeight * baseScale) / 2f

        matrix.reset()
        matrix.setScale(baseScale, baseScale)
        matrix.postTranslate(dx, dy)
        currentScale = 1.0f

        ivFullReceipt.scaleType = ImageView.ScaleType.MATRIX
        ivFullReceipt.imageMatrix = matrix
    }

    private fun clampMatrix() {
        val d = ivFullReceipt.drawable ?: return
        val dWidth = d.intrinsicWidth.toFloat()
        val dHeight = d.intrinsicHeight.toFloat()
        val vWidth = ivFullReceipt.width.toFloat()
        val vHeight = ivFullReceipt.height.toFloat()
        if (dWidth <= 0 || dHeight <= 0 || vWidth <= 0 || vHeight <= 0) return

        val rect = RectF(0f, 0f, dWidth, dHeight)
        matrix.mapRect(rect)

        var deltaX = 0f
        var deltaY = 0f

        if (rect.width() <= vWidth) {
            deltaX = (vWidth - rect.width()) / 2f - rect.left
        } else {
            if (rect.left > 0f) deltaX = -rect.left
            if (rect.right < vWidth) deltaX = vWidth - rect.right
        }

        if (rect.height() <= vHeight) {
            deltaY = (vHeight - rect.height()) / 2f - rect.top
        } else {
            if (rect.top > 0f) deltaY = -rect.top
            if (rect.bottom < vHeight) deltaY = vHeight - rect.bottom
        }

        matrix.postTranslate(deltaX, deltaY)
    }

    private fun setupZoomAndPan(imageView: ImageView) {
        var isScaling = false
        var lastX = 0f
        var lastY = 0f

        val scaleDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    isScaling = true
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val factor = detector.scaleFactor
                    val targetScale = (currentScale * factor).coerceIn(1.0f, 5.0f)
                    val actualFactor = targetScale / currentScale
                    currentScale = targetScale

                    matrix.postScale(actualFactor, actualFactor, detector.focusX, detector.focusY)
                    clampMatrix()
                    imageView.imageMatrix = matrix
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    isScaling = false
                }
            }
        )

        val gestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (currentScale > 1.2f) {
                        resetToFitCenter()
                    } else {
                        val targetScale = 2.5f
                        val actualFactor = targetScale / currentScale
                        currentScale = targetScale
                        matrix.postScale(actualFactor, actualFactor, e.x, e.y)
                        clampMatrix()
                        imageView.imageMatrix = matrix
                    }
                    return true
                }
            }
        )

        imageView.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isScaling && currentScale > 1.01f) {
                        val dx = event.x - lastX
                        val dy = event.y - lastY
                        matrix.postTranslate(dx, dy)
                        clampMatrix()
                        imageView.imageMatrix = matrix
                    }
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isScaling = false
                    if (currentScale <= 1.01f) {
                        resetToFitCenter()
                    }
                }
            }
            true
        }
    }

    companion object {
        const val TAG = "ReceiptViewerDialogFragment"
        private const val ARG_RECEIPT_PATH = "arg_receipt_path"

        fun newInstance(path: String): ReceiptViewerDialogFragment {
            return ReceiptViewerDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_RECEIPT_PATH, path)
                }
            }
        }
    }
}
