package tomohiro.takeuchi.android.smarttravel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_image.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import tomohiro.takeuchi.android.smarttravel.model.TravelDetail
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Handler
import androidx.core.app.ActivityCompat
import android.widget.ImageView
import org.jetbrains.anko.noButton
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class EditImageActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private lateinit var dm :DisplayMetrics
    private val resultActionGetContent = 1000
    private var manageId = 1
    private var day = 1
    private var order = 0
    private var filePath = ""
    private var imageFlag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        realm = Realm.getDefaultInstance()
        dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        manageId = intent.getIntExtra("manageId", 1)
        day = intent.getIntExtra("day", 1)
        order = intent.getIntExtra("order", 0)

        imageEditImage1.layoutParams.width = dm.widthPixels / 3
        imageEditImage2.layoutParams.width = dm.widthPixels / 3
        imageEditImage3.layoutParams.width = dm.widthPixels / 3
        imageEditImage4.layoutParams.width = dm.widthPixels / 3
        imageEditImage1.layoutParams.height = dm.heightPixels / 3
        imageEditImage2.layoutParams.height = dm.heightPixels / 3
        imageEditImage3.layoutParams.height = dm.heightPixels / 3
        imageEditImage4.layoutParams.height = dm.heightPixels / 3

        //外部ストレージパーミッションチェック
        if (hasPermission()) {
            if (isExternalStorageReadable()){
                setViews()
            }else {
                alert(resources.getString(R.string.storageInvalid)) {
                    yesButton { finish() }
                }.show()
            }
        }
    }

    //イメージビューの表示
    private fun setViews() {
        val file = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: null
        val travelDetail = realm.where<TravelDetail>()
            .equalTo("manageId", manageId)
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst()

        if (file != null && travelDetail is TravelDetail){
            val text = "- Day$day : " + travelDetail.destination + " -"
            imageEditGuide.text = text

            filePath = file.path
            Log.i("【EditImageActivity】", "[setViews] filePath=$filePath")

            if (travelDetail.imageUrl1.isNotEmpty() && travelDetail.imageUrl1 != ""){
                val bitmap1 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl1}")
                if (bitmap1 != null) {
                    imageButtonDelete1.visibility = View.VISIBLE
                    setBitmap(bitmap1, imageEditImage1)
                }
            }
            if (travelDetail.imageUrl2.isNotEmpty() && travelDetail.imageUrl2 != ""){
                val bitmap2 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl2}")
                if (bitmap2 != null) {
                    imageButtonDelete2.visibility = View.VISIBLE
                    setBitmap(bitmap2, imageEditImage2)
                }
            }
            if (travelDetail.imageUrl3.isNotEmpty() && travelDetail.imageUrl3 != ""){
                val bitmap3 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl3}")
                if (bitmap3 != null) {
                    imageButtonDelete3.visibility = View.VISIBLE
                    setBitmap(bitmap3, imageEditImage3)
                }
            }
            if (travelDetail.imageUrl4.isNotEmpty() && travelDetail.imageUrl4 != ""){
                val bitmap4 = loadPictureFromLocal("$filePath/${travelDetail.imageUrl3}")
                if (bitmap4 != null) {
                    imageButtonDelete4.visibility = View.VISIBLE
                    setBitmap(bitmap4, imageEditImage4)
                }
            }
        }
        setImageViewLink()
    }

    //画像が存在する場合、表示する。
    private fun setBitmap(bitmap: Bitmap, imageEditImage: ImageView?) {
        val viewBitmap = Bitmap.createScaledBitmap(bitmap
            ,dm.widthPixels/3, dm.heightPixels/4, true)
        imageEditImage?.setImageBitmap(viewBitmap)
    }

    //imageViewクリック時の動作
    private fun setImageViewLink() {
        imageEditImage1.setOnClickListener {
            it.notPressTwice()
            imageClick(1)
        }
        imageEditImage2.setOnClickListener {
            it.notPressTwice()
            imageClick(2)
        }
        imageEditImage3.setOnClickListener {
            it.notPressTwice()
            imageClick(3)
        }
        imageEditImage4.setOnClickListener {
            it.notPressTwice()
            imageClick(4)
        }
        imageEditBack.setOnClickListener {
            it.notPressTwice()
            finish()
        }
        imageButtonDelete1.setOnClickListener {
            it.notPressTwice()
            conformDelete(1)
        }
        imageButtonDelete2.setOnClickListener {
            it.notPressTwice()
            conformDelete(2)
        }
        imageButtonDelete3.setOnClickListener {
            it.notPressTwice()
            conformDelete(3)
        }
        imageButtonDelete4.setOnClickListener {
            it.notPressTwice()
            conformDelete(4)
        }
    }

    //削除してよいか確認。
    private fun conformDelete(imageNum: Int) {
        alert (resources.getString(R.string.deleteConform)) {
            yesButton { imageDelete(imageNum) }
            noButton {  }
        }.show()
    }

    //削除（Realmを空にする、かつ、imageViewをデフォルトに）
    private fun imageDelete(imageNum: Int) {
        when(imageNum){
            1 -> {
                imageEditImage1.setImageResource(R.drawable.ic_image_black_24dp)
                imageButtonDelete1.visibility = View.INVISIBLE
            }
            2 -> {
                imageEditImage2.setImageResource(R.drawable.ic_image_black_24dp)
                imageButtonDelete2.visibility = View.INVISIBLE
            }
            3 -> {
                imageEditImage3.setImageResource(R.drawable.ic_image_black_24dp)
                imageButtonDelete3.visibility = View.INVISIBLE
            }
            else -> {
                imageEditImage4.setImageResource(R.drawable.ic_image_black_24dp)
                imageButtonDelete4.visibility = View.INVISIBLE
            }
        }
        realm.executeTransaction {
            realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", order)
                .findFirst()
                ?.let {
                   when(imageNum){
                       1 -> it.imageUrl1 = ""
                       2 -> it.imageUrl2 = ""
                       3 -> it.imageUrl3 = ""
                       else -> it.imageUrl4 = ""
                   }
                }
        }
    }

    //画像をクリックしたときの動作
    private fun imageClick(imageNum: Int) {
        imageFlag = imageNum
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, resultActionGetContent)
    }

    //ユーザーがピッカーでドキュメントを選択すると、onActivityResult() が呼び出される
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageView = when (imageFlag){
            1 -> {
                imageButtonDelete1.visibility = View.VISIBLE
                imageEditImage1
            }
            2 -> {
                imageButtonDelete2.visibility = View.VISIBLE
                imageEditImage2
            }
            3 -> {
                imageButtonDelete3.visibility = View.VISIBLE
                imageEditImage3
            }
            else -> {
                imageButtonDelete4.visibility = View.VISIBLE
                imageEditImage4
            }
        }

        //requestCode, resultCodeが正しい場合、
        if(requestCode == resultActionGetContent && resultCode == Activity.RESULT_OK) {
            val handler = Handler()
            val timeStamp = getTimeStamp()
            val travelDetail = realm.where<TravelDetail>()
                .equalTo("manageId", manageId)
                .equalTo("day", day)
                .equalTo("order", order)
                .findFirstAsync()
            realm.executeTransaction {
                when (imageFlag){
                    1 -> travelDetail?.imageUrl1 = "$timeStamp.png"
                    2 -> travelDetail?.imageUrl2 = "$timeStamp.png"
                    3 -> travelDetail?.imageUrl3 = "$timeStamp.png"
                    else -> travelDetail?.imageUrl4 = "$timeStamp.png"
                }
            }
            Thread{
                var output: FileOutputStream? = null
                try{
                    val uri = data?.data
                    if (uri is Uri) {
                        Log.i("【EditImageActivity】", "[onActivityResult] uri=$uri")
                        val bitmap = getBitmapFromUri(uri)
                        handler.post{
                            imageView.setImageBitmap(bitmap)
                        }
                        output = FileOutputStream("$filePath/$timeStamp.png")
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                        output.flush()
                    }
                }catch (e: IOException){
                    finish()
                }finally {
                    try {
                        output?.close()
                    }catch (e: IOException){
                        finish()
                    }
                }
            }.start()
        }
    }

    //タイムスタンプを受け取る。
    private fun getTimeStamp(): String {
        val date = Date()
        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return "TRAVEL_${format.format(date)}"
    }

    //UriをBitmapに変換するメソッド
    private fun getBitmapFromUri(uri: Uri): Bitmap{
        val parcelFileDescriptor:ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri,"r")
        if (parcelFileDescriptor is ParcelFileDescriptor){
            val fileDescriptor:FileDescriptor = parcelFileDescriptor.fileDescriptor
            val image:Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        }
        throw IOException(resources.getString(R.string.failToReadFile))
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
                finish()
            }
        }
        return res
    }

    //外部ストレージアクセスのパーミッション確認
    private fun hasPermission(): Boolean {
        //パーミッションを持っているか確認
        if (ContextCompat.checkSelfPermission(this
                , android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //　持っていないならパーミッション要求
            ActivityCompat.requestPermissions(this
                , arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return false
        }
        return true
    }

    //パーミッションの結果を受け取る。
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            setViews()
        }else{
            alert(resources.getString(R.string.pleaseAllowStorage)) {
                yesButton { finish() }
            }.show()
        }
    }

    //外部ストレージが使用可能かどうかをチェックする。
    private fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        Log.i("【EditImageActivity】", "[isExternalStorageReadable]")
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    /**
     * 二度押し防止施策として 0.5 秒間タップを禁止する
     */
    private fun View.notPressTwice() {
        this.isEnabled = false
        this.postDelayed({
            this.isEnabled = true
        }, 1500L)
    }

    //アクティビティ削除時にRealmを閉じる。
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}