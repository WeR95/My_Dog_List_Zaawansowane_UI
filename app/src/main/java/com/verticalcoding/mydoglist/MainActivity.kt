package com.verticalcoding.mydoglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verticalcoding.mydoglist.ui.theme.MyDogListTheme

data class Dog(val name: String, var isFavorite: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDogListTheme {
                DogListApp()
            }
        }
    }
}

@Composable
fun DogListApp() {
    var dogName by remember { mutableStateOf("") }
    var dogs by remember { mutableStateOf(mutableSetOf<Dog>()) }
    var errorMessage by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var favoriteCount by remember { mutableStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = dogName,
                    onValueChange = {
                        dogName = it
                        errorMessage = ""
                    },
                    label = { Text("Wyszukaj lub dodaj psa") },
                    isError = errorMessage.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.dog),
                            contentDescription = "Dog Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))


                IconButton(
                    onClick = {
                        searchQuery = dogName
                    },
                    enabled = dogName.isNotEmpty()
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Szukaj psa")
                }

                Spacer(modifier = Modifier.width(8.dp))


                IconButton(
                    onClick = {
                        if (dogs.any { it.name.equals(dogName, ignoreCase = true) }) {
                            errorMessage = "Pies o tej nazwie już istnieje!"
                        } else {
                            dogs = dogs.toMutableSet().apply { add(Dog(name = dogName)) }
                            dogName = ""
                            errorMessage = ""
                        }
                    },
                    enabled = dogName.isNotEmpty()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj psa")
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.dog),
                        contentDescription = "All Dogs",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ": ${dogs.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorite Dogs", tint = Color.Red)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ": $favoriteCount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                val filteredDogs = if (searchQuery.isEmpty()) {
                    dogs
                } else {
                    dogs.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                items(filteredDogs.sortedByDescending { it.isFavorite }) { dog ->
                    DogItem(
                        dog = dog,
                        onDelete = {
                            dogs = dogs.filter { it != dog }.toMutableSet()
                            favoriteCount = dogs.count { it.isFavorite }
                        },
                        onFavorite = { updatedDog ->
                            dogs = dogs.map {
                                if (it.name == updatedDog.name) it.copy(isFavorite = !it.isFavorite)
                                else it
                            }.toMutableSet()
                            favoriteCount = dogs.count { it.isFavorite }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DogItem(dog: Dog, onDelete: () -> Unit, onFavorite: (Dog) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFEEB6E9), Color(0xFF65558F))
                    ),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.dog),
                contentDescription = "Dog Icon",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = dog.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = {
            onFavorite(dog)
        }) {

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        shadowElevation = 4.dp.toPx()
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFEEB6E9), Color(0xFF65558F))
                        ),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Add to Favorite",
                    modifier = Modifier.matchParentSize(),
                    tint = if (dog.isFavorite) Color.Transparent else Color.Gray
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Usuń psa", tint = Color.Black)
        }
    }
}