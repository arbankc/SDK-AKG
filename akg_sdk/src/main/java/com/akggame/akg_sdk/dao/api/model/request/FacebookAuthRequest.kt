package com.akggame.akg_sdk.dao.api.model.request

import androidx.annotation.Keep

@Keep
class FacebookAuthRequest {

    /**
     * access_token : EAAN9LULZASj0BAJTkvozxhOpLvv9Gn6erQmO21yHMDalcitjZC0FWSVLtPibVzLzFS6izrXKvQT9lq0pZAgeRXXxtXYUOi6qpDe9jDfzgky3v5irya08dNSKgZC39lnrW8NRe1uhcdGDFb2F0uf3b4xw4pDZBPC4MWP6pOafC2Dc7hz69UqPqaskIHpbOBFbFSZAu06hm3OQZDZD
     * auth_provider : facebook
     * game_provider : lockdown
     * device_id : 2
     * phone_model : Iphone X
     * operating_system : Ios
     */

    var access_token: String? = null
    var auth_provider: String? = null
    var game_provider: String? = null
    var device_id: String? = null
    var phone_model: String? = null
    var operating_system: String? = null
    var expires_in: Int? = 0
    var refresh_token: String? = null

}
