package com.example.memoriesv3.Fragments

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.memoriesv3.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import android.graphics.drawable.BitmapDrawable
import android.widget.EditText
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

private lateinit var photo_button: Button
private lateinit var save_button: Button
private lateinit var name_field: EditText
private lateinit var image_view: ImageView
private const val REQUEST_CODE = 110
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewMemoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewMemoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_new_memory, container, false)
    }

    //Initialize global variables and create a listener for the photo button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        photo_button = view.findViewById(R.id.button_take_picture)
        save_button = view.findViewById(R.id.button_save)
        name_field = view.findViewById(R.id.editText)
        image_view = view.findViewById(R.id.new_memory_fragment)
        NewMemoryFragment.Companion

        photo_button.setOnClickListener {
            val take_picture_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(take_picture_intent, REQUEST_CODE)
            name_field.setText("Give it a name...")
        }

        save_button.setOnClickListener {
            val takenPicture = image_view.drawable.toBitmap()
            val name: String = name_field.text.toString().trim()
            if (!name.isNullOrEmpty())
            {
                saveToStorage(takenPicture, name)
                name_field.isVisible = false
                save_button.isVisible = false
                photo_button.isVisible = true
            }
            else
                Toast.makeText(this.context,"You must name your memory!",Toast.LENGTH_SHORT).show()
        }
        //super.onViewCreated(view, savedInstanceState)
    }

    //Function for saving a bitmap picture to phone storage
    private fun saveToStorage(photo: Bitmap, name: String){
        val cw = ContextWrapper(this.context)
        val directory = cw.getDir("photos", Context.MODE_PRIVATE)
        val file_path = File(directory, "${name}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file_path)
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            Toast.makeText(this.context,"Error saving the photo to storage...",Toast.LENGTH_SHORT).show()
        }
    }

    //Result activity after user takes the picture
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenPicture = data?.extras?.get("data") as Bitmap
            image_view.setImageBitmap(takenPicture)
            name_field.isVisible = true
            save_button.isVisible = true
            photo_button.isVisible = false
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewMemoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewMemoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}