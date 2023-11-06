# FFBad API header tokens

The FFBad API is exposed as a backend for the website [MyFFBad](https://www.myffbad.fr/accueil)

This app uses 2 API endpoints to fetch available tournaments : `/api/search/` and `/api/tournament/{id}/informations`

Both of these APIs requires custom headers named `Verify-Token`

## Retrieve your tokens
In order to retrieve your tokens you need to [log in on MyFFBad](https://www.myffbad.fr/connexion) website using your player credentials

Both tokens only need to be retrieved once 

### Search Verify-Token
Once logged in, you can retrieve your `Verify-Token` for the search API on the [tournament page](https://www.myffbad.fr/recherche/tournoi)

Using the browser console, find the `/api/search/` POST request and find the request header named `Verify-Token`

### Details Verify-Token
Once logged in, you can retrieve your `Verify-Token` for the tournament details API on any tournament details page, [like this one](https://www.myffbad.fr/tournoi/details/2302670)

Using the browser console, find the `/api/tournament/{id}/informations` POST request and find the request header named `Verify-Token`
