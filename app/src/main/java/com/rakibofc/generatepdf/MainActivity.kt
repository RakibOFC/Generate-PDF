package com.rakibofc.generatepdf

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var pageHeight = 1120
    private var pageWidth = 792

    private lateinit var bmp: Bitmap
    private lateinit var scaledbmp: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bmp = BitmapFactory.decodeResource(resources, android.R.drawable.ic_dialog_email)
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)

        findViewById<Button>(R.id.btnGenPdf).setOnClickListener {
            // generatePDF()

            if (Build.VERSION.SDK_INT >= 33)
                convertXmlToPdf()
            else
                requestForFilePermission()
        }
    }

    private fun generatePDF() {

        val pdfDocument = PdfDocument()

        val paint = Paint()
        val title = Paint()

        val myPageInfo: PageInfo? =
            PageInfo.Builder(pageWidth, pageHeight, 1).create()

        val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)

        // creating a variable for canvas
        // from our page of PDF.
        val canvas = myPage.canvas

        canvas.drawBitmap(scaledbmp, 56F, 40F, paint)

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.textSize = 15F

        title.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))

        canvas.drawText("A portal for IT professionals.", 209F, 100F, title)
        canvas.drawText("Geeks for Geeks", 209F, 80F, title)
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        title.textSize = 15F

        title.textAlign = Paint.Align.CENTER
        canvas.drawText("This is sample document which we have created.", 396F, 560F, title)

        pdfDocument.finishPage(myPage)

        /*// Check if the WRITE_EXTERNAL_STORAGE permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_STORAGE_PERMISSION
            )
        } else {
            // Permission is already granted, proceed with file write
            // writeFile()
        }*/

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "example.pdf"
        val file = File(downloadsDir, fileName)
        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            Toast.makeText(this, "Written Successfully!!!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("TAG", "Error while writing ${e.message}")
        }

        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close()
    }

    private val request_code_file_permission = 101

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == request_code_file_permission) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file write
                convertXmlToPdf()
            } else {
                // Permission denied, show a message or handle it gracefully
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestForFilePermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                request_code_file_permission
            )
        } else {
            // Permission is already granted, proceed with file write
            convertXmlToPdf()
        }
    }

    private fun convertXmlToPdf() {

        // Inflate the XML layout file
        val view: View = LayoutInflater.from(this).inflate(R.layout.activity_main, null)
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display!!.getRealMetrics(displayMetrics)
        } else {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        view.measure(
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
        )

        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        // Create a new PdfDocument instance
        val document = PdfDocument()

        // Obtain the width and height of the view
        val viewWidth = view.measuredWidth;
        val viewHeight = view.measuredHeight;

        val pageInfo = PageInfo.Builder(viewWidth, viewHeight, 1).create()

        // Start a new page
        val page = document.startPage(pageInfo)

        // Get the Canvas object to draw on the page
        val canvas = page.canvas

        // Create a Paint object for styling the view
        val paint = Paint()
        paint.setColor(Color.WHITE)

        // Draw the view on the canvas
        view.draw(canvas)

        // Finish the page
        document.finishPage(page)

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "exampleXML1.pdf"
        val filePath = File(downloadsDir, fileName)

        try {
            // Save the document to a file
            val fos = FileOutputStream(filePath)
            document.writeTo(fos)
            document.close()
            fos.close()
            // PDF conversion successful
            Toast.makeText(this, "XML to PDF Conversion Successful", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            Log.e("TAG", "convertXmlToPdf: ${e.message}")
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}