package com.example.mascota

object Routes {

    /* =======================
       COMMON
       ======================= */
    const val SPLASH = "splash"
    const val ROLE = "role"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // client - crear paseo
    const val CLIENT_WALKERS_NEARBY = "client/walkers/nearby"
    const val CLIENT_WALKER_DETAIL = "client/walkers/detail"      // + /{walkerId}
    const val CLIENT_WALK_REQUEST = "client/walks/request"        // + /{walkerId}

    /* =======================
       CLIENT
       ======================= */
    const val CLIENT_HOME = "client/home"
    const val CLIENT_PETS = "client/pets"
    const val CLIENT_PET_FORM = "client/pets/form"

    // Paseos cliente
    const val CLIENT_WALKS = "client/walks"
    const val CLIENT_WALK_DETAIL = "client/walks/detail" // + /{walkId}

    /* =======================
       WALKER
       ======================= */
    // dueño: mapa del paseo (solo EN CURSO)
    const val CLIENT_WALK_MAP = "client/walks/map"          // + /{walkId}

    // dueño: calificar paseo (Finalizado)
    const val CLIENT_WALK_REVIEW = "client/walks/review"    // + /{walkId}


    // elegir dirección antes de ver paseadores
    const val CLIENT_CHOOSE_ADDRESS = "client/addresses/choose"

    const val WALKER_HOME = "walker/home"

    // Paseos paseador
    const val WALKER_WALKS = "walker/walks"
    const val WALKER_WALK_DETAIL = "walker/walks/detail" // + /{walkId}

    // Reseñas paseador
    const val WALKER_REVIEWS = "walker/reviews"
    const val WALKER_REVIEW_DETAIL = "walker/reviews/detail" // + /{reviewId}
}
