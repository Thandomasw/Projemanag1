package za.tmoney.projemanag1.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import za.tmoney.projemanag1.R

object Constants {
    const val USERS : String = "users"
    const val BOARDS : String = "boards"

    const val IMAGE : String = "image"
    const val MOBILE : String = "mobile"
    const val NAME : String = "name"
    const val ASSIGNED_TO : String = "assignedTo"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST : String = "taskList"

    const val BOARD_DETAIL : String = "board_detail"
    const val ID : String = "id"
    const val EMAIL: String = "email"

    const val BOARD_MEMBERS_LIST: String = "board_members_list"

    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"

    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"





}