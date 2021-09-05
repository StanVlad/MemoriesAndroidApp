package com.example.memoriesv3.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoriesv3.GridItemAdapter
import com.example.memoriesv3.Memory
import com.example.memoriesv3.R
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.memoriesv3.BuildConfig
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PastMemoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PastMemoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycler_view: RecyclerView
    private lateinit var photos: Array<Memory>
    private lateinit var selected_photos: ArrayList<Memory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_memories, container, false)
    }

    //Function to read all pictures from storage and return
    private fun createMemoriesArray(): Array<Memory>
    {
        var list: MutableList<Memory> = mutableListOf<Memory>()
        File("/data/user/0/com.example.memoriesv3/app_photos").walkTopDown().forEach {
            if (it.isFile)
                if (it.name.substringAfterLast('.', "") == "jpg" && it.nameWithoutExtension.length <= 30) {
                    val memory: Memory = Memory()
                    memory.set_name(it.nameWithoutExtension)
                    memory.set_uri(Uri.fromFile(it))
                    list.add(memory)
                }
        }
        return list.toTypedArray()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        photos = createMemoriesArray()
        selected_photos = photos.copyOf().toCollection(ArrayList())

        val adapter = GridItemAdapter(selected_photos)
        val grid_layout = GridLayoutManager(this.context, 1)
        recycler_view = view.findViewById<RecyclerView>(R.id.recycler_view)
        recycler_view.layoutManager = grid_layout
        recycler_view.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search_bar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                selected_photos.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())

                if (searchText.isNotEmpty()) {
                    photos.forEach {
                        if (it.get_name().lowercase(Locale.getDefault()).contains(searchText)){
                            selected_photos.add(it)
                            println("->"+it.get_name())
                        }
                    }
                    recycler_view.adapter?.notifyDataSetChanged()
                } else {
                    selected_photos.clear()
                    selected_photos.addAll(photos)
                    recycler_view.adapter?.notifyDataSetChanged()
                }

                return true
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        //item ids are declared inside the GridItemAdapter
        when(item.itemId){
            301 -> {
                //get original file referenced by grid item
                val itemName = selected_photos[item.groupId].get_name()
                val full_path = "/data/user/0/com.example.memoriesv3/app_photos/" + itemName + ".jpg"
                val file = File(full_path)
                val bitmapImage = BitmapFactory.decodeFile(file.absolutePath)

                //temporarily copy image file to cache directory for sharing
                val images_folder : File = this.context!!.cacheDir
                val temp_img_id = UUID.randomUUID().toString()
                val cache_file = File(images_folder, temp_img_id+".jpg")
                try {
                    val stream: OutputStream = FileOutputStream(cache_file)
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    stream.close()
                } catch (e: IOException){
                    Toast.makeText(this.context,"Error saving the photo to storage...",Toast.LENGTH_SHORT).show()
                }
                //val file_uri: Uri = Uri.fromFile(file)

                //trigger share intent for established image file URI
                val file_uri = FileProvider.getUriForFile(this.context!!, BuildConfig.APPLICATION_ID+".provider", cache_file)
                Toast.makeText(this.context,file_uri.toString(),Toast.LENGTH_SHORT).show()
                val share = Intent(Intent.ACTION_SEND)
                //share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.type = "image/jpeg"
                share.putExtra(Intent.EXTRA_STREAM, file_uri)
                startActivity(Intent.createChooser(share, "Share my memory..."))
            }
        }
        return super.onContextItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PastMemoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PastMemoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}