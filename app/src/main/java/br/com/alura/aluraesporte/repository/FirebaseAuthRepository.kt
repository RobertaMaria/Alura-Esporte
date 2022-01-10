package br.com.alura.aluraesporte.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.aluraesporte.model.Usuario
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.lang.Exception
import java.lang.IllegalArgumentException

private const val TAG = "Firebase"

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun desloga() {
        firebaseAuth.signOut()
    }

    fun autenticaUsuario(usuario: Usuario): LiveData<Resource<Boolean>> {
        var mutableLiveData = MutableLiveData<Resource<Boolean>>()
        try {
            firebaseAuth.signInWithEmailAndPassword(usuario.email, usuario.senha)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mutableLiveData.value = Resource(true)
                    }else{
                        val mensagemErro: String = devolveErroAutenticacao(it.exception)
                        mutableLiveData.value = Resource(false, mensagemErro)
                    }
                }
        } catch (e: IllegalArgumentException) {
            mutableLiveData.value = Resource(false, "E-mail ou senha não pode ser vazio")
        }
        return mutableLiveData
    }

    private fun devolveErroAutenticacao(exception: Exception?): String {
       return when (exception) {
            is FirebaseAuthInvalidUserException,
            is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválido"
            else -> "Erro desconhecido"
        }

    }

    fun cadastraUsuario(usuario: Usuario): LiveData<Resource<Boolean>> {
        var livedata = MutableLiveData<Resource<Boolean>>()

        try {
            val tarefa = firebaseAuth.createUserWithEmailAndPassword(usuario.email, usuario.senha)
            tarefa.addOnSuccessListener {
                Log.i(TAG, "cadastro: Cadastro sucedido.")
                livedata.value = Resource(dado = true)
            }
            tarefa.addOnFailureListener { exception ->
                Log.e(TAG, "cadastro: Cadastro falhou.", exception)
                val mensagemErro = devolveErroDeCadastro(exception)
                livedata.value = Resource(dado = false, erro = mensagemErro)
            }
        } catch (e: IllegalArgumentException) {
            livedata.value = Resource(dado = false, erro = "E-mail ou senha não pode ser vazio.")
        }
        return livedata
    }

    private fun devolveErroDeCadastro(exception: Exception): String {
        val mensagemErro: String = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Senha precisa de pelo menos 6 dígitos"
            is FirebaseAuthInvalidCredentialsException -> "E-mail inválido"
            is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
            else -> "erro desconhecido"

        }
        return mensagemErro
    }

    fun estaLogado(): Boolean {
        val usuarioFirebase = firebaseAuth.currentUser
        if (usuarioFirebase != null) {
            return true
        }
        return false
    }

    fun usuario(): LiveData<Usuario> {
        val liveData = MutableLiveData<Usuario>()
        firebaseAuth.currentUser?.let {
            it.email?.let {
                liveData.value = Usuario(it)
            }
        }
        return liveData

    }
}