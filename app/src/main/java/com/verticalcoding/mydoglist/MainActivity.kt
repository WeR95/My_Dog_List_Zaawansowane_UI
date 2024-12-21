package com.verticalcoding.mydoglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class Dog(val name: String, val breed: String, var isFavorite: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "DogList") {
                composable("DogList") {
                    DogListApp(navController)
                }
                composable("DogScreen/{dogName}") { backStackEntry ->
                    val dogName = backStackEntry.arguments?.getString("dogName") ?: "Unknown Dog"
                    val dog = getDogByName(dogName)
                    dog?.let {
                        DogDetailsScreen(navController, it) { deletedDog ->
                            dogList.remove(deletedDog)
                            navController.popBackStack()
                        }
                    }
                }
                composable("Settings") {
                    SettingsScreen(navController)
                }
                composable("Profile") {
                    ProfileScreen(navController)
                }
            }
        }
    }

    private fun getDogByName(name: String): Dog? {
        return dogList.find { it.name == name }
    }
}

val dogList = mutableStateListOf(
    Dog(name = "Bobby", breed = "Labrador"),
    Dog(name = "Max", breed = "Beagle"),
    Dog(name = "Rex", breed = "German Shepherd")
)

val dogNamesSet = mutableSetOf<String>()

@Composable
fun DogListApp(navController: NavController) {
    var dogName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val favoriteCount by remember { derivedStateOf { dogList.count { it.isFavorite } } }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { navController.navigate("Settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }


                    Text(
                        text = "Doggos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )


                    IconButton(onClick = { navController.navigate("Profile") }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


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
                        if (dogNamesSet.contains(dogName.lowercase())) {
                            errorMessage = "Pies o tej nazwie już istnieje!"
                        } else {
                            dogList.add(Dog(name = dogName, breed = "Unknown"))
                            dogNamesSet.add(dogName.lowercase())
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
                        text = ": ${dogList.size}",
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
                    dogList
                } else {
                    dogList.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                items(filteredDogs.sortedByDescending { it.isFavorite }) { dog ->
                    DogItem(
                        dog = dog,
                        onDelete = {
                            dogList.remove(dog)
                            dogNamesSet.remove(dog.name.lowercase())
                        },
                        onFavorite = { updatedDog ->
                            val index = dogList.indexOfFirst { it.name == updatedDog.name }
                            if (index != -1) {
                                dogList[index] = updatedDog.copy(isFavorite = !updatedDog.isFavorite)
                            }
                        },
                        onClick = {
                            navController.navigate("DogScreen/${dog.name}")
                        }
                    )
                }
            }
        }
    }
}




@Composable
fun DogItem(dog: Dog, onDelete: () -> Unit, onFavorite: (Dog) -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFEEB6E9), Color(0xFF65558F))
                    )
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = dog.breed,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dog.name,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = { onFavorite(dog) }) {
            Icon(
                imageVector = if (dog.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite"
            )
        }

        IconButton(onClick = { onDelete() }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete"
            )
        }
    }
}


@Composable
fun DogDetailsScreen(navController: NavController, dog: Dog, onDelete: (Dog) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigate("DogList")
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Detale",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )


            Spacer(modifier = Modifier.weight(1f))


            IconButton(onClick = {
                onDelete(dog)
                navController.navigate("DogList")
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Dog",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFEEB6E9), Color(0xFF65558F))
                    )
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.dog),
                contentDescription = "Dog Icon",
                modifier = Modifier.align(Alignment.Center).size(100.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = dog.breed,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = dog.name,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(onClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigate("DogList")
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Ustawienia",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(onClick = {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    navController.navigate("DogList")
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Profil",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jan Brzechwa",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
