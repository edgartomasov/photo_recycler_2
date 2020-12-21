package com.et.photo_recycler_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.et.photo_recycler.adapters.PhotoAdapter
import com.et.photo_recycler.api.NetworkApi
import com.et.photo_recycler.api.RetrofitClient
import com.et.photo_recycler.model.PhotoModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private var title: TextView?= null
    private var photo_recycler: RecyclerView?= null

    private val service: NetworkApi get() = RetrofitClient.createClient(NetworkApi::class.java)
    private var photoListMain: ArrayList<PhotoModel> = ArrayList()
    private var photoList: ArrayList<PhotoModel> = ArrayList()

    private var photoAdapter: PhotoAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = findViewById(R.id.title)
        photo_recycler = findViewById(R.id.photo_recycler)
        getPhoto()
    }

    private fun getPhoto(){
        service.getPhoto()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { v ->
                            photoListMain.addAll(v)
                            if (photoListMain.isNotEmpty()) {
                                createList()
                            } else {
                                // Nothing
                            }
                        },
                        { e -> println("Error: $e")}
                )

    }

    private fun createList(){
        photoList.clear()

        photoList.add(photoListMain[photoListMain.size-1])
        photoList.addAll(photoListMain)
        photoList.add(photoListMain[0])

        photoAdapter = PhotoAdapter(photoList)
        photo_recycler?.adapter = photoAdapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        photo_recycler?.layoutManager = layoutManager

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(photo_recycler)

        photo_recycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    val first = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (first >= 0){
                        title!!.text = photoList[first].title
                        when (first) {
                            0 -> {
                                photo_recycler!!.scrollToPosition(photoList.size-2)
                            }
                            photoList.size-1 -> {
                                photo_recycler!!.scrollToPosition(1)
                            }
                            else -> {
                                // Nothing
                            }
                        }
                    }  else {
                        // Nothing
                    }
                } else {
                    // Nothing
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        title!!.text = photoList[1].title
        photo_recycler!!.scrollToPosition(1)
    }
}