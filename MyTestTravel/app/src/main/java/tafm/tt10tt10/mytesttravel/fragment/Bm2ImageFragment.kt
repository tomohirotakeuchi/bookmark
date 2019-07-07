package tafm.tt10tt10.mytesttravel.fragment

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.bm2_image_fragment.*
import tafm.tt10tt10.mytesttravel.R
import tafm.tt10tt10.mytesttravel.model.TravelDetail
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class Bm2ImageFragment : Fragment() {

    private lateinit var realm: Realm
    private lateinit var dm :DisplayMetrics
    private var listener: Bm2ImageEditOnClickListener? = null
    private var manageId = 1
    private var day = 1
    private var order = 0
    private var filePath = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bm2_image_fragment, container, false)
        val argument = arguments
        argument?.apply{
            manageId = this["manageId"] as Int
            day = this["day"] as Int
            order = this["order"] as Int
            Log.i("【Bm2ImageFragment】", "[onCreateView] !!arguments!! manageId=$manageId day=$day order=$order")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()
        dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)

        //編集のクリックリスナー実装
        view.findViewById<ImageView>(R.id.bm2_image_edit).setOnClickListener {
            it.notPressTwice()
            listener = context as? Bm2ImageEditOnClickListener
            listener?.onImageEditClick()
        }

        bm2_image_Image1.layoutParams.width = dm.widthPixels / 3
        bm2_image_Image2.layoutParams.width = dm.widthPixels / 3
        bm2_image_Image3.layoutParams.width = dm.widthPixels / 3
        bm2_image_Image4.layoutParams.width = dm.widthPixels / 3
        bm2_image_Image1.layoutParams.height = dm.heightPixels / 3
        bm2_image_Image2.layoutParams.height = dm.heightPixels / 3
        bm2_image_Image3.layoutParams.height = dm.heightPixels / 3
        bm2_image_Image4.layoutParams.height = dm.heightPixels / 3

        val file = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()

        if (file != null && travelDetail is TravelDetail) setImages(file, travelDetail)
        setImageViewsLinks()
    }

    //ImageViewをクリックしたときの処理。
    private fun setImageViewsLinks() {
        bm2_image_Image1.setOnClickListener {
            it.notPressTwice()
            setImageViewExpand(1)
        }
        bm2_image_Image2.setOnClickListener {
            it.notPressTwice()
            setImageViewExpand(2)
        }
        bm2_image_Image3.setOnClickListener {
            it.notPressTwice()
            setImageViewExpand(3)
        }
        bm2_image_Image4.setOnClickListener {
            it.notPressTwice()
            setImageViewExpand(4)
        }
    }

    //画像をImageViewに表示。
    private fun setImages(file: File, travelDetail: TravelDetail) {
        filePath = file.path
        Log.i("【Bm2ImageFragment】", "[setImages] filePath=$filePath")
        if (travelDetail.imageUrl1.isNotEmpty() && travelDetail.imageUrl1 != ""){
            val bitmap1 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl1}")
            if (bitmap1 != null) setBitmap(bitmap1, bm2_image_Image1)
        }
        if (travelDetail.imageUrl2.isNotEmpty() && travelDetail.imageUrl2 != ""){
            val bitmap2 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl2}")
            if (bitmap2 != null) setBitmap(bitmap2, bm2_image_Image2)
        }
        if (travelDetail.imageUrl3.isNotEmpty() && travelDetail.imageUrl3 != ""){
            val bitmap3 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl3}")
            if (bitmap3 != null) setBitmap(bitmap3, bm2_image_Image3)
        }
    }

    //Dialogで画像をタップしたときに拡大表示。
    private fun setImageViewExpand(number: Int) {
        val cxt = context ?: return
        val filename = getFileName(number)

        if (filename != null && filename != "") {
            val bitmap = loadPictureFromLocal("$filePath/$filename")
            if (bitmap != null){
                val imageView = ImageView(cxt)
                imageView.setImageBitmap(bitmap)
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                val dialog = Dialog(cxt)
                dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.window?.setLayout(dm.widthPixels, dm.heightPixels)
                dialog.setContentView(imageView)
                dialog.show()
                imageView.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    //データベースの値からファイルネームを取得して返却。
    private fun getFileName(number: Int): String? {
        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()
        return when(number){
            2 -> travelDetail?.imageUrl2
            3 -> travelDetail?.imageUrl3
            4 -> travelDetail?.imageUrl4
            else -> travelDetail?.imageUrl1
        }
    }

    //ビットマップの大きさを画面サイズからリサイズして表示。
    private fun setBitmap(bitmap: Bitmap, image: ImageView?) {
        val viewBitmap = Bitmap.createScaledBitmap(bitmap
            ,dm.widthPixels/3, dm.heightPixels/4, true)
        image?.setImageBitmap(viewBitmap)
    }

    //画像をパスから引っ張ってくる。
    private fun loadPictureFromLocal(path: String): Bitmap? {
        Log.i("【Bm2ImageFragment】", "[loadPictureFromLocal] path=$path")
        val res: Bitmap?
        var input: FileInputStream? = null
        try {
            input = FileInputStream(path)
            res = BitmapFactory.decodeStream(input)
        }catch (e: FileNotFoundException){
            Log.i("【Bm2ImageFragment】", "[loadPictureFromLocal] FileNotFound")
            return null
        }catch (e: IOException){
            Log.i("【Bm2ImageFragment】", "[loadPictureFromLocal] IOException")
            return null
        }finally {
            try {
                input?.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        return res
    }

    //Bm2ImageEditのinterface。
    interface Bm2ImageEditOnClickListener{
        fun onImageEditClick()
    }

    /**
     * 二度押し防止施策として 1.5 秒間タップを禁止する
     */
    private fun View.notPressTwice() {
        this.isEnabled = false
        this.postDelayed({
            this.isEnabled = true
        }, 1500L)
    }

    //フラグメント削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}