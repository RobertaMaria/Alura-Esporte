package br.com.alura.aluraesporte.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.alura.aluraesporte.R
import br.com.alura.aluraesporte.extensions.snackBar
import br.com.alura.aluraesporte.model.Produto
import br.com.alura.aluraesporte.ui.viewmodel.ComponentesVisuais
import br.com.alura.aluraesporte.ui.viewmodel.EstadoAppViewModel
import br.com.alura.aluraesporte.ui.viewmodel.FormularioProdutoViewlModel
import kotlinx.android.synthetic.main.detalhes_produto.*
import kotlinx.android.synthetic.main.formulario_produto.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.math.BigDecimal


class FormularioProdutoFragment : BaseFragment() {

    private val arguments by navArgs<FormularioProdutoFragmentArgs>()
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: FormularioProdutoViewlModel by viewModel()
    private val controlador by lazy {
        findNavController()
    }
    private val produtoAd by lazy {
        arguments.produtoId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.formulario_produto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tentaBuscarProduto()

        estadoAppViewModel.temComponentes = ComponentesVisuais(appBar = true, bottomNavigation = false)

        configuraBotaoSalvar()
    }

    private fun configuraBotaoSalvar() {
        formulario_produto_botao_salva.setOnClickListener {
            val produto = criaProduto()
            salvaProduto(produto)
        }
    }

    private fun salvaProduto(produto: Produto) {
        viewModel.salva(produto).observe(viewLifecycleOwner, Observer { salvo ->
            salvo?.let {
                if (salvo) {
                    controlador.popBackStack()
                    return@Observer
                }
                view?.snackBar("Falha ao salvar o rpoduto")
            }
        })
    }

    private fun criaProduto(): Produto {
        val nome = formulario_produto_campo_nome.editText?.text.toString()
        val preco = formulario_produto_campo_preco.editText?.text.toString()
        val produto = Produto(idFirestore = produtoAd, nome = nome, preco = BigDecimal(preco))
        return produto
    }

    private fun tentaBuscarProduto() {
        produtoAd?.let { id ->
            viewModel.buscaPorId(id).observe(viewLifecycleOwner) { produto ->
                val nome = produto.nome
                val preco = produto.preco.toString()

                formulario_produto_campo_nome.editText?.setText(nome)
                formulario_produto_campo_preco.editText?.setText(preco)
                requireActivity().title = "Altera produto"
            }
        }
    }

}