package com.example.lojarodrigo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val TAG = "LojaApp"

class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    var estoque: Int
) {
    fun exibirDetalhes() {
        Log.d(TAG, "Produto: $nome, Preço: R$$preco, Estoque: $estoque")
    }
}

class Cliente(
    val id: Int,
    val nome: String,
    var saldo: Double
) {
    fun adicionarSaldo(valor: Double) {
        saldo += valor
        Log.d(TAG, "Novo saldo de $nome: R$$saldo")
    }
}

class CarrinhoDeCompras {
    val produtos = mutableListOf<Pair<Produto, Int>>()

    fun adicionarProduto(produto: Produto, quantidade: Int) {
        if (produto.estoque >= quantidade) {
            produtos.add(produto to quantidade)
            Log.d(TAG, "${quantidade}x ${produto.nome} adicionado ao carrinho.")
        } else {
            Log.d(TAG, "Estoque insuficiente para ${produto.nome}.")
        }
    }

    fun removerProduto(produto: Produto) {
        produtos.removeIf { it.first == produto }
        Log.d(TAG, "${produto.nome} removido do carrinho.")
    }

    fun exibirCarrinho() {
        produtos.forEach { Log.d(TAG, "${it.second}x ${it.first.nome} - R$${it.first.preco}") }
    }

    fun calcularTotal(): Double {
        return produtos.sumOf { it.first.preco * it.second }
    }
}

class Loja {
    val produtosDisponiveis = mutableListOf(
        Produto(1, "Camiseta", 50.0, 10),
        Produto(2, "Calça", 100.0, 5),
        Produto(3, "Tênis", 200.0, 8)
    )

    fun listarProdutos() {
        produtosDisponiveis.forEach { it.exibirDetalhes() }
    }

    fun finalizarCompra(cliente: Cliente, carrinho: CarrinhoDeCompras) {
        val total = carrinho.calcularTotal()
        if (cliente.saldo >= total) {
            carrinho.produtos.forEach { (produto, quantidade) ->
                produto.estoque -= quantidade
            }
            cliente.saldo -= total
            Log.d(TAG, "Compra realizada com sucesso!")
        } else {
            Log.d(TAG, "Saldo insuficiente!")
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LojaScreen()
        }
    }
}

@Composable
fun LojaScreen() {
    val loja = remember { Loja() }
    val cliente = remember { Cliente(1, "Rodrigo", 500.0) }
    val carrinho = remember { CarrinhoDeCompras() }
    var mensagem by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Produtos Disponíveis:", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(loja.produtosDisponiveis) { produto ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                    carrinho.adicionarProduto(produto, 1)
                    mensagem = "${produto.nome} adicionado ao carrinho!"
                }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${produto.nome} - R$${produto.preco}")
                        Text("Estoque: ${produto.estoque}")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            loja.finalizarCompra(cliente, carrinho)
            mensagem = "Compra finalizada! Saldo restante: R$${cliente.saldo}"
        }) {
            Text("Finalizar Compra")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(mensagem, style = MaterialTheme.typography.bodySmall)
    }
}