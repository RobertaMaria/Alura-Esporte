package br.com.alura.aluraesporte.repository


import android.util.Log
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "Firebase"

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    private fun desloga(firebaseAuth: FirebaseAuth) {
        firebaseAuth.signOut()
    }

    private fun verificaSeOUsuarioEstaLogado(firebaseAuth: FirebaseAuth) {
        val usuarioFirebase = firebaseAuth.currentUser
        if (usuarioFirebase != null) {

        } else {

        }
    }

    private fun autenticaUsuario(firebaseAuth: FirebaseAuth) {
        firebaseAuth.signInWithEmailAndPassword("roberta@aluraesporte.com", "123456")
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

     fun cadastraUsuario(email: String, senha: String) {
        val tarefa =
            firebaseAuth.createUserWithEmailAndPassword(email, senha)
        tarefa.addOnSuccessListener {
            Log.i(TAG, "cadastro: Cadastro sucedido.")
        }
        tarefa.addOnFailureListener {
            Log.e(TAG, "cadastro: Cadastro falhou.", it)
        }
    }
}