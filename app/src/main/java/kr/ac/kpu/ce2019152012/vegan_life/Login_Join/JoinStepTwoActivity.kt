package kr.ac.kpu.ce2019152012.vegan_life.Login_Join

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestoreSettings
import kr.ac.kpu.ce2019152012.vegan_life.DataVo.JoinDataVo
import kr.ac.kpu.ce2019152012.vegan_life.R
import kr.ac.kpu.ce2019152012.vegan_life.databinding.ActivityJoinTwoBinding

class JoinStepTwoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinTwoBinding

    private var auth: FirebaseAuth? = null
    private lateinit var db : FirebaseFirestore

    var VeganType: Int = 0
    /*
    vegan = 1 , lacto = 2 , obo = 3 , lactoObo = 4 , fesco = 5
    */
    var SexType: Int = 0
    /*
    female = 2 , male = 1
    */
    var basicCal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setup2()

        val myData = intent?.getParcelableExtra<JoinDataVo>("Join")
        val text =
            "JoinData{${myData?.profileImage}, ${myData?.nickname}, ${myData?.email}, ${myData?.password}}"

        binding.vegan.setOnClickListener {
            binding.vegan.isSelected = binding.vegan.isSelected != true
            binding.lacto.isSelected = false
            binding.obo.isSelected = false
            binding.lactoObo.isSelected = false
            binding.fesco.isSelected = false

            if(binding.vegan.isSelected){
                VeganType = 1
            } else {
                VeganType = 0
            }
        }

        binding.lacto.setOnClickListener {
            binding.lacto.isSelected = binding.lacto.isSelected != true
            binding.vegan.isSelected = false
            binding.obo.isSelected = false
            binding.lactoObo.isSelected = false
            binding.fesco.isSelected = false

            if(binding.lacto.isSelected){
                VeganType = 2
            } else {
                VeganType = 0
            }
        }
        binding.obo.setOnClickListener {
            binding.obo.isSelected = binding.obo.isSelected != true
            binding.vegan.isSelected = false
            binding.lacto.isSelected = false
            binding.lactoObo.isSelected = false
            binding.fesco.isSelected = false

            if(binding.obo.isSelected){
                VeganType = 3
            } else {
                VeganType = 0
            }
        }
        binding.lactoObo.setOnClickListener {
            binding.lactoObo.isSelected = binding.lactoObo.isSelected != true
            binding.vegan.isSelected = false
            binding.lacto.isSelected = false
            binding.obo.isSelected = false
            binding.fesco.isSelected = false

            if(binding.lactoObo.isSelected){
                VeganType = 4
            } else {
                VeganType = 0
            }
        }
        binding.fesco.setOnClickListener {
            binding.fesco.isSelected = binding.fesco.isSelected != true
            binding.vegan.isSelected = false
            binding.lacto.isSelected = false
            binding.obo.isSelected = false
            binding.lactoObo.isSelected = false

            if(binding.fesco.isSelected){
                VeganType = 5
            } else {
                VeganType = 0
            }
        }

        binding.txtFemale.setOnClickListener {
            binding.txtFemale.isSelected = binding.txtFemale.isSelected != true
            binding.txtMale.isSelected = false

            if(binding.txtFemale.isSelected){
                SexType = 2
                basicCal = 655.1 + (9.56 * binding.joinWeight.text.toString().toDouble())
                + (1.85 * binding.joinHeight.text.toString().toDouble()) - (4.68 * binding.joinAge.text.toString().toDouble())
            } else {
                SexType = 0
            }
        }
        binding.txtMale.setOnClickListener {
            binding.txtMale.isSelected = binding.txtMale.isSelected != true
            binding.txtFemale.isSelected = false

            if(binding.txtMale.isSelected){
                SexType = 1
                basicCal = 66.47 + (13.75 * binding.joinWeight.text.toString().toDouble()) + (5.0 * binding.joinHeight.text.toString().toDouble()) - (6.76 * binding.joinAge.text.toString().toDouble())
            } else {
                SexType = 0
            }
        }

        binding.nextBtn.setOnClickListener {
            if (VeganType == 0 && SexType == 0) {
                Toast.makeText(this, "??????????????? ???????????????????????????.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(
                    "Tag", "OneActivity?????? ????????? ??????: " + text +
                            "?????? ??????: ${VeganType}, ????????? ??????: ${SexType}" +
                            "???:${
                                binding.joinHeight.text.toString().trim()
                            }, ?????????: ${binding.joinWeight.text.toString().trim()}")

                val UserInformation = hashMapOf(
                    "email" to myData?.email,
                    "passwd" to myData?.password,
                    "nickname" to myData?.nickname,
                    "image" to myData?.profileImage,
                    "hieght" to binding.joinHeight.text.toString().trim(),
                    "weight" to binding.joinWeight.text.toString().trim(),
                    "age" to binding.joinAge.text.toString().trim(),
                    "basiccal" to basicCal,
                    "vegantype" to VeganType,
                    "sex" to SexType
                )

                db.collection(myData!!.email).document("Info")
                    .set(UserInformation, SetOptions.merge())
                    .addOnCanceledListener { Log.d(TAG, "DocumentSnapshot successfully written!")}
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)  }

                createUser(myData.email,myData.password)
                val intent = Intent(this,JoinCompleteActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun createUser(email: String, password: String) {
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                // ????????? ?????? ??????
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (task.isSuccessful) {
                        Toast.makeText(this, "???????????? ??????", Toast.LENGTH_SHORT).show()
                        val user = auth?.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "???????????? ??????", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                } else {
                    Toast.makeText(this, "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    fun setup2(){
        db = FirebaseFirestore.getInstance()
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}