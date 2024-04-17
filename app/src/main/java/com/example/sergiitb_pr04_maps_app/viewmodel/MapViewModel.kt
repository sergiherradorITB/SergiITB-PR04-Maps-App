package com.example.sergiitb_pr04_maps_app.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.model.Categoria
import com.example.sergiitb_pr04_maps_app.model.MarkerSergi
import com.example.sergiitb_pr04_maps_app.model.Repository
import com.example.sergiitb_pr04_maps_app.model.UserPrefs
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapViewModel : ViewModel() {

    var editedTitle by mutableStateOf("")
        private set

    var editedSnippet by mutableStateOf("")
        private set

    var editedCategoryName by mutableStateOf("")
        private set

    var editedPhoto by mutableStateOf<Bitmap?>(null)
        private set

    var showTakePhotoScreen by mutableStateOf(false)
        private set

    var showDialog by mutableStateOf(false)
        private set

    fun modificarEditedTitle(title: String) {
        editedTitle = title
    }

    fun modificarEditedSnippet(snippet: String) {
        editedSnippet = snippet
    }

    fun modificarEditedPhoto(photo: Bitmap?) {
        editedPhoto = photo
    }

    fun modificarCategoryName(categoryName: String) {
        editedCategoryName = categoryName
    }

    fun modificarShowTakePhotoScreen(value: Boolean) {
        showTakePhotoScreen = value
    }

    fun modificarShowDialog(value: Boolean) {
        showDialog = value
    }

    private val title = mutableStateOf("")
    private val snippet = mutableStateOf("")
    private val selectedCategoria = mutableStateOf<Categoria?>(null)
    private val photoBitmap = mutableStateOf<Bitmap?>(null)
    private val _uriFoto = mutableStateOf<Uri?>(null)
    var uriFoto = _uriFoto

    private val photoTaken = mutableStateOf(false)
    private val showGuapo = mutableStateOf(false)

    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPositionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    private val database = FirebaseFirestore.getInstance()

    // LiveData para la lista de marcadores
    private val _markers = MutableLiveData<MutableList<MarkerSergi>>()
    val markers: LiveData<MutableList<MarkerSergi>> = _markers

    var repository: Repository = Repository()

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied
    }

    fun modifyTitle(newValue: String) {
        title.value = newValue
    }

    fun getTitle(): String {
        return title.value
    }

    fun modifySnippet(newValue: String) {
        snippet.value = newValue
    }

    fun getSnippet(): String {
        return snippet.value
    }

    fun modifySelectedCategory(newValue: Categoria?) {
        selectedCategoria.value = newValue
    }

    fun getSelectedCategory(): Categoria? {
        return selectedCategoria.value
    }

    fun modifyUriPhoto(newValue: Uri?) {
        _uriFoto.value = newValue
    }

    fun pillarUriPhoto(): String {
        return uriFoto.value.toString()
    }

    fun modifyPhotoBitmap(newValue: Bitmap?) {
        photoBitmap.value = newValue
    }

    fun getPhotoBitmap(): Bitmap? {
        return photoBitmap.value
    }

    fun modifyPhotoTaken(newValue: Boolean) {
        photoTaken.value = newValue
    }

    fun getPhotoTaken(): Boolean {
        return photoTaken.value
    }

    fun modifyShowGuapo(newValue: Boolean) {
        showGuapo.value = newValue
    }

    fun getShowGuapo(): Boolean {
        return showGuapo.value
    }

    var expanded by mutableStateOf(false)
        private set

    fun modifyExpanded(valorNuevo: Boolean) {
        expanded = valorNuevo
    }

    fun pillarExpanded(): Boolean {
        return expanded
    }

    var expandedMapa by mutableStateOf(false)
        private set

    fun modifyExpandedMapa(valorNuevo: Boolean) {
        expandedMapa = valorNuevo
    }

    fun pillarExpandedMapa(): Boolean {
        return expandedMapa
    }

    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(positionNueva: LatLng) {
        position = positionNueva
    }

    fun getPosition(): LatLng {
        return position
    }

    // LiveData para la lista de marcadores
    private val _editingMarkers = MutableLiveData<MarkerSergi>()
    var editingMarkers: LiveData<MarkerSergi> = _editingMarkers

    fun setEditingMarkers(marker: MarkerSergi) {
        _editingMarkers.value = marker
    }

    private val _categories = MutableLiveData<MutableList<Categoria>>()
    val categories: LiveData<MutableList<Categoria>> = _categories

    private val _textoDropDown = MutableLiveData<String>()
    val textoDropdown: LiveData<String> = _textoDropDown

    private val _showBottomSheet = MutableLiveData<Boolean>()
    val showBottomSheet = _showBottomSheet

    fun modificarShowBottomSheet(nuevoBoolean: Boolean) {
        _showBottomSheet.value = nuevoBoolean
    }

    fun modificarTextoDropdown(nuevoTexto: String) {
        _textoDropDown.value = nuevoTexto
    }

    init {
        // Inicializar la lista de categorías si es necesario
        if (_categories.value == null) {
            _categories.value = mutableListOf(
                Categoria("Info", Color.Green),
                Categoria("Likes", Color.Yellow),
                Categoria("Favoritos", Color.Cyan)
            )
        }
    }

    // Método para obtener la lista de categorías
    fun getCategories(): List<Categoria> {
        return _categories.value.orEmpty()
    }

    fun deleteMarker(markerId: String) {
        database.collection("markers").document(markerId).delete()
    }

    fun updateMarker(editedMarker: MarkerSergi) {
        if (uriFoto.value != null) {
            uploadImage(uriFoto.value!!, editedMarker) { downloadUrl ->
                // Actualizar la referencia de la foto en el marcador con la URL de descarga
                editedMarker.modificarPhotoReference(downloadUrl)

                // Agregar el marcador a la base de datos con la referencia de la foto actualizada
                database.collection("markers").document(editedMarker.markerId!!)
                    .set(
                        hashMapOf(
                            "owner" to _loggedUser.value,
                            "positionLatitude" to editedMarker.latitude,
                            "positionLongitude" to editedMarker.longitude,
                            "title" to editedMarker.title,
                            "snippet" to editedMarker.snippet,
                            "categoryName" to editedMarker.category.name,
                            "linkImage" to editedMarker.photoReference
                        )
                    )
                    .addOnSuccessListener {
                        println("Marker añadido correctamente a la base de datos")
                        // Solicitar la lista completa de marcadores después de añadir uno nuevo
                        pillarTodosMarkers()
                    }
                    .addOnFailureListener { e ->
                        println("Error al añadir el marker a la base de datos: ${e.message}")
                    }

            }
        } else {
            // Agregar el marcador a la base de datos con la referencia de la foto actualizada
            database.collection("markers").document(editedMarker.markerId!!)
                .set(
                    hashMapOf(
                        "owner" to _loggedUser.value,
                        "positionLatitude" to editedMarker.latitude,
                        "positionLongitude" to editedMarker.longitude,
                        "title" to editedMarker.title,
                        "snippet" to editedMarker.snippet,
                        "categoryName" to editedMarker.category.name,
                        "linkImage" to editedMarker.photoReference
                    )
                )
                .addOnSuccessListener {
                    println("Marker añadido correctamente a la base de datos")
                    // Solicitar la lista completa de marcadores después de añadir uno nuevo
                    pillarTodosMarkers()
                }
                .addOnFailureListener { e ->
                    println("Error al añadir el marker a la base de datos: ${e.message}")
                }
        }
    }

    fun addMarkerToDatabase(marker: MarkerSergi) {
        uploadImage(uriFoto.value!!, marker) { downloadUrl ->
            // Actualizar la referencia de la foto en el marcador con la URL de descarga
            marker.modificarPhotoReference(downloadUrl)

            // Agregar el marcador a la base de datos con la referencia de la foto actualizada
            database.collection("markers")
                .add(
                    hashMapOf(
                        "owner" to _loggedUser.value,
                        "positionLatitude" to marker.latitude,
                        "positionLongitude" to marker.longitude,
                        "title" to marker.title,
                        "snippet" to marker.snippet,
                        "categoryName" to marker.category.name,
                        "linkImage" to marker.photoReference
                    )
                )
                .addOnSuccessListener {
                    println("Marker añadido correctamente a la base de datos")
                    // Solicitar la lista completa de marcadores después de añadir uno nuevo
                    pillarTodosMarkers()
                }
                .addOnFailureListener { e ->
                    println("Error al añadir el marker a la base de datos: ${e.message}")
                }
        }
    }

    fun uploadImage(imageUri: Uri, marker: MarkerSergi, onComplete: (String) -> Unit) {
        println("XAVI UWU")
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")

        storage.putFile(imageUri)
            .addOnSuccessListener { uploadTask ->
                Log.i("IMAGE UPLOAD", "Image uploaded successfully")
                println("Xavi peruano fino")
                uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println("URL de descarga de la imagen: $downloadUrl")
                    onComplete(downloadUrl) // Llamar a la funcion que le pasamos, en este caso le pasamos la de añadir marcador a la bbdd
                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image upload failed")
                println("Xavi peruano malo")
            }
    }

    fun uploadImage(imageUri: Uri, onComplete: (String) -> Unit) {
        println("XAVI UWU")
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")

        storage.putFile(imageUri)
            .addOnSuccessListener { uploadTask ->
                Log.i("IMAGE UPLOAD", "Image uploaded successfully")
                println("Xavi peruano fino")
                uploadTask.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println("URL de descarga de la imagen: $downloadUrl")
                    onComplete(downloadUrl) // Llamar a la funcion que le pasamos, en este caso le pasamos la de añadir marcador a la bbdd
                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image upload failed")
                println("Xavi peruano malo")
            }
    }

    fun pillarTodosMarkers() {
        repository.getMarkers()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<MarkerSergi>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(MarkerSergi::class.java)
                        newMarker.markerId = dc.document.id
                        newMarker.latitude =
                            dc.document.get("positionLatitude").toString().toDouble()
                        newMarker.longitude =
                            dc.document.get("positionLongitude").toString().toDouble()
                        newMarker.category.name = dc.document.get("categoryName").toString()
                        newMarker.photoReference = dc.document.get("linkImage").toString()
                        tempList.add(newMarker)
                        println("Adios :( " + newMarker)
                    }

                }
                _markers.value = tempList
            }
    }

    fun pillarTodosMarkersCategoria(categoria: String) {
        repository.getMarkers()
            .whereEqualTo("owner", _loggedUser.value)
            .whereEqualTo("categoryName", categoria)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<MarkerSergi>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(MarkerSergi::class.java)
                        newMarker.markerId = dc.document.id
                        newMarker.latitude =
                            dc.document.get("positionLatitude").toString().toDouble()
                        newMarker.longitude =
                            dc.document.get("positionLongitude").toString().toDouble()
                        newMarker.category.name = dc.document.get("categoryName").toString()
                        newMarker.photoReference = dc.document.get("linkImage").toString()
                        tempList.add(newMarker)
                        println("Adios :( " + newMarker.category.name)
                    }

                }
                _markers.value = tempList
            }
    }

    private val auth = FirebaseAuth.getInstance()

    fun userLogged(): Boolean {
        val resultat: Boolean = if (auth.currentUser == null) {
            false
        } else {
            true
        }
        return resultat
    }

    // LiveData para isLoading
    private val _goToNext = MutableLiveData<Boolean>()
    val goToNext = _goToNext

    private val _userId = MutableLiveData<String>()
    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser
    fun pillarLoggedUser(): String {
        return _loggedUser.value.toString()
    }

    fun modificarLoggedUser(nuevo: String) {
        _loggedUser.value = nuevo
    }

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading = _isLoading

    // LiveData para emailState
    private val _emailState = MutableLiveData<String>()
    val emailState: LiveData<String> = _emailState

    // LiveData para passwordState
    private val _passwordState = MutableLiveData<String>()
    val passwordState: LiveData<String> = _passwordState

    // LiveData para passwordState
    private val _nombreState = MutableLiveData<String>()
    val nombreState: LiveData<String> = _nombreState


    // LiveData para passwordState
    private val _apellidoState = MutableLiveData<String>()
    val apellidoState: LiveData<String> = _apellidoState

    // LiveData para passwordState
    private val _ciudadState = MutableLiveData<String>()
    val ciudadState: LiveData<String> = _ciudadState

    // LiveData para showDialog
    private val _showDialogPass = MutableLiveData<Boolean>()
    val showDialogPass: LiveData<Boolean> = _showDialogPass

    // LiveData para passwordProblem
    private val _passwordProblem = MutableLiveData<Boolean>()
    val passwordProblem: LiveData<Boolean> = _passwordProblem

    fun modificarEmailState(value: String) {
        _emailState.value = value
    }

    fun modificarPasswordState(value: String) {
        _passwordState.value = value
    }

    fun modificarNombreState(value: String) {
        _nombreState.value = value
    }

    fun modificarApellidoState(value: String) {
        _apellidoState.value = value
    }

    fun modificarCiudadState(value: String) {
        _ciudadState.value = value
    }

    fun modificarShowDialogPass(value: Boolean) {
        _showDialogPass.value = value
    }

    fun modificarPasswordProblem(value: Boolean) {
        _passwordProblem.value = value
    }

    fun modifyProcessing(newValue: Boolean) {
        _isLoading.value = newValue
    }

    private val _showDialogAuth = MutableLiveData<Boolean>()
    val showDialogAuth: LiveData<Boolean> = _showDialogAuth

    fun modificarShowDialogAuth(value: Boolean) {
        _showDialogAuth.value = value
    }

    private val _emailDuplicated = MutableLiveData<Boolean>()
    val emailDuplicated: LiveData<Boolean> = _emailDuplicated


    fun register(context: Context, username: String, password: String) {
        val userPrefs = UserPrefs(context)
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                    modifyProcessing(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(_emailState.value!!, _passwordState.value!!)
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                // Si no hay documentos para este usuario, agregar uno nuevo
                                database.collection("user")
                                    .add(
                                        hashMapOf(
                                            "owner" to _loggedUser.value,
                                            "name" to _nombreState.value,
                                            "apellido" to _apellidoState.value,
                                            "ciudad" to _ciudadState.value,
                                            // "password" to usuari.password (es logico guardar la contraseña rarete, no?)
                                        )
                                    )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user : ${task.exception}")
                    modifyProcessing(true)
                    _emailDuplicated.value = true
                    _showDialogAuth.value = true
                }
            }
    }

    private val _validLogin = MutableLiveData<Boolean>()
    val validLogin: LiveData<Boolean> = _validLogin

    private val _passwordVisibility = MutableLiveData<Boolean>()
    val passwordVisibility = _passwordVisibility

    fun cambiarPassVisibility(nuevoBoolean: Boolean) {
        _passwordVisibility.value = nuevoBoolean
    }

    private val _permanecerLogged = MutableLiveData<Boolean>()
    val permanecerLogged = _permanecerLogged

    fun cambiarPermanecerLogged(nuevoBoolean: Boolean) {
        _permanecerLogged.value = nuevoBoolean
    }


    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.toString()
                    _goToNext.value = true
                    modifyProcessing(false)
                    // Agregar el marcador a la base de datos con la referencia de la foto actualizada
                    // Verificar si el usuario ya tiene un documento en la colección "user"
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                // Si no hay documentos para este usuario, agregar uno nuevo
                                database.collection("user")
                                    .add(
                                        hashMapOf(
                                            "owner" to _loggedUser.value,
                                            "name" to _nombreState.value,
                                            "apellido" to _apellidoState.value,
                                            "ciudad" to _ciudadState.value,
                                            // "password" to usuari.password (es logico guardar la contraseña rarete, no?)
                                        )
                                    )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in: ${task.exception}")
                    modifyProcessing(true)
                    _emailDuplicated.value = false
                    _showDialogAuth.value = true
                }
            }
            .addOnFailureListener { task ->
                _validLogin.value = false
            }
    }

    fun signOut(context: Context, navController: NavController) {

        val userPrefs = UserPrefs(context)

        CoroutineScope(Dispatchers.IO).launch {
            userPrefs.deleteUserData()
        }

        auth.signOut()

        _goToNext.value = false
        _passwordState.value = ""

        modifyProcessing(true)
        navController.navigate(Routes.LogScreen.route)
    }


    private val _imageUrlForUser = MutableLiveData<String>()
    val imageUrlForUser = _imageUrlForUser

    fun getProfileImageUrlForUser() {
        repository.getUserImageUri()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }

                var tempString: String =
                    "https://firebasestorage.googleapis.com/v0/b/pueseso-5f478.appspot.com/o/images%2Fuser.webp?alt=media&token=965b2876-019f-433d-8ffe-56f6c216bab1"

                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            tempString = dc.document.getString("image") ?: tempString
                            println("Un peruano encontrado")
                            println(tempString)
                        }
                    }
                }

                _imageUrlForUser.value = tempString
            }
    }

    fun updateUser() {
        uploadImage(uriFoto.value!!) { downloadUrl ->
            // Realizar una consulta para encontrar el documento del usuario
            database.collection("user")
                .whereEqualTo("owner", _loggedUser.value)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Actualizar el documento con la nueva información
                        val data = mutableMapOf<String, Any>("image" to downloadUrl)
                        document.reference.update(data)
                            .addOnSuccessListener {
                                println("Usuario actualizado correctamente en la base de datos")
                            }
                            .addOnFailureListener { e ->
                                println("Error al actualizar el usuario en la base de datos: ${e.message}")
                            }
                    }
                    getProfileImageUrlForUser()
                }
                .addOnFailureListener { exception ->
                    println("Error al consultar la base de datos: ${exception.message}")
                }
        }
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            modifyProcessing(false)
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MascotaFeliz", "Log con exito")
                            val userRef =
                                database.collection("user").whereEqualTo("owner", _loggedUser.value)
                            userRef.get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        // Si no hay documentos para este usuario, agregar uno nuevo
                                        database.collection("user")
                                            .add(
                                                hashMapOf(
                                                    "owner" to _loggedUser.value,
                                                    // "name" to _nombreState.value,
                                                    // "apellido" to _apellidoState.value,
                                                    // "ciudad" to _ciudadState.value,
                                                    // "password" to usuari.password (es logico guardar la contraseña rarete, no?)
                                                )
                                            )
                                    }
                                }
                            home()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("MascotaFeliz", "Fallo al loguear")
                    }
            } catch (ex: Exception) {
                Log.d("MascotaFeliz", "Excepción al hacer log" + ex.localizedMessage)
            }
        }
}