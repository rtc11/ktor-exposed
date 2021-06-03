package route

import kotlinx.serialization.Serializable

@Serializable
data class Authority(val name: String, val ids: List<String>)

@Serializable
data class Municipality(val id: String, val name: String)

@Serializable
data class Stop(val id: String, val name: String, val municipality: Municipality)
