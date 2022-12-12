package com.example.sqlite

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


@Suppress("DEPRECATION")
class ContactActivity : AppCompatActivity() {

    private lateinit var buttonBack: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonDelete: Button
    private lateinit var name: TextView
    private lateinit var surname: TextView
    private lateinit var birthData: TextView
    private lateinit var phone: TextView

    private val dbHelper = DBHelper(this)


    val REQUEST_CODE = 2

    private lateinit var idGlobal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact)
        val id = intent.getStringExtra(MainActivity.EXTRA_KEY)
        idGlobal = id.toString()

        name = findViewById(R.id.name)
        surname = findViewById(R.id.surname)
        birthData = findViewById(R.id.birthData)
        phone = findViewById(R.id.phoneNumber)

        buttonBack = findViewById(R.id.buttonBack)
        buttonDelete = findViewById(R.id.buttonDelete)
        buttonEdit = findViewById(R.id.buttonEdit)

        val contact = (dbHelper.getById(id.toString().toInt()))

        name.text = contact?.name
        surname.text = contact?.surname
        birthData.text = contact?.birthData
        phone.text = contact?.phoneNumber

        phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.text))
            startActivity(intent)
        }

        buttonBack.setOnClickListener {
            finishActivity()
        }
        buttonDelete.setOnClickListener {
            val listId = id.toString().toInt()
            dialogYesOrNo(
                this, "Удалить контакт?", "Точно?")
                { dialog, id ->
                dbHelper.removeContact(listId)
                finishActivity()
            }
        }
        buttonEdit.setOnClickListener {
            val intent = Intent(this, ContactEditActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_KEY, id.toString())
            startActivityForResult(intent, REQUEST_CODE)
        }
    }
        fun dialogYesOrNo(
            activity: Activity,
            title: String,
            message: String,
            listener: DialogInterface.OnClickListener
        ) {
            val builder = AlertDialog.Builder(activity)
            builder.setPositiveButton("Yes") { dialog, id ->
                dialog.dismiss()
                listener.onClick(dialog, id)
            }
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.setTitle(title)
            alert.setMessage(message)
            alert.show()
        }

        fun finishActivity() {
            val returnIntent = Intent()
            returnIntent.putExtra(ContactEditActivity.RESULT_KEY, "1")
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(ContactEditActivity.RESULT_KEY)
            val listContacts = dbHelper.getContacts()
            for (contact in listContacts) {
                if (contact.id.toString() == idGlobal) {
                    name.text = contact.name
                    surname.text = contact.surname
                    birthData.text = contact.birthData
                    phone.text = contact.phoneNumber
                    break
                }
            }

        }
    }
}