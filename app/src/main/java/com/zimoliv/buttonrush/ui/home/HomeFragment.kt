package com.zimoliv.buttonrush.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zimoliv.buttonrush.domain.CustomActionView
import com.zimoliv.buttonrush.MainActivity2
import com.zimoliv.buttonrush.domain.dialog.PseudoDialog
import com.zimoliv.buttonrush.R
import com.zimoliv.buttonrush.databinding.FragmentHomeBinding
import com.zimoliv.buttonrush.domain.dialog.BravoDialog


class HomeFragment : Fragment(), PauseDialogListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var chronoTextView: TextView
    lateinit var textClickNumber: TextView
    lateinit var pauseButton : FloatingActionButton
    lateinit var countdownTextView : TextView
    lateinit var contain2 : RelativeLayout
    lateinit var cardViewCircular : CircularRevealCardView

    private lateinit var countDownTimer: CountDownTimer
    private var elapsedTime: Long = 0
    private var countDownTimer2: CountDownTimer? = null

    private lateinit var radioGroup1 : RadioGroup
    private lateinit var radioGroup2 : RadioGroup

    private lateinit var buttonStart : Button
    private lateinit var rankedCardView : CircularRevealCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        chronoTextView = binding.chrnonometerHome
        textClickNumber = binding.numberClickText
        pauseButton = binding.pauseButton
        countdownTextView = binding.countdownTextView2
        contain2 = binding.container2
        cardViewCircular = binding.cardViewCircular
        radioGroup1 = binding.radio1
        radioGroup2 = binding.radio2
        buttonStart = binding.startButtonOrange
        buttonStart.isEnabled = false
        rankedCardView = binding.trophyCardview
        rankedCardView.visibility = View.GONE
        countDownTimer = object : CountDownTimer(86400000 - elapsedTime, 10) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime += 10
                (activity as MainActivity2).setTime(elapsedTime)
                updateChronoText()
            }
            override fun onFinish() {
                resetPage()
                Toast.makeText(requireContext(), getString(R.string.day_too_late), Toast.LENGTH_LONG).show()
            }
        }

        val number1 = (activity as MainActivity2).getNumberClick()
        val goal1 = (activity as MainActivity2).getGoal()
        elapsedTime = (activity as MainActivity2).getTime()

        if (number1 > 0) {
            (activity as MainActivity2).showPauseDialog(updateChronoText(),("$number1/$goal1"), this)
            chronoTextView.visibility = View.VISIBLE
            textClickNumber.visibility = View.VISIBLE
            cardViewCircular.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            val goal3 = (activity as MainActivity2).getGoal()
            textClickNumber.text = "$number1/$goal3"
        }

        binding.buttonClick.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    vibratePhone(50)
                    // Lorsqu'un doigt touche le bouton
                    // Votre logique ici
                    true // Indiquer que l'événement est consommé
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    when (event.pointerCount) {
                        2 -> {
                            vibratePhone(50)
                            // Lorsqu'un deuxième doigt touche le bouton
                            // Votre logique ici pour deux doigts
                            true // Indiquer que l'événement est consommé
                        }
                        3 -> {
                            vibratePhone(50)
                            // Lorsqu'un troisième doigt touche le bouton
                            // Votre logique ici pour trois doigts
                            true // Indiquer que l'événement est consommé
                        }
                        else -> {
                            false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                        }
                    }
                }
                else -> false // Indiquer que l'événement n'est pas consommé pour d'autres actions
            }
        }
        pauseButton.setOnClickListener {
            var number2 = (activity as MainActivity2).getNumberClick()
            if (number2 < 0) {
                number2 = 0
            }
            val goal2 = (activity as MainActivity2).getGoal()
            (activity as MainActivity2).setTime(elapsedTime)
            countDownTimer.cancel()
            (activity as MainActivity2).showPauseDialog(updateChronoText(),("$number2/$goal2"), this)
            binding.buttonClick.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        vibratePhone(50)
                        // Lorsqu'un doigt touche le bouton
                        // Votre logique ici
                        true // Indiquer que l'événement est consommé
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        when (event.pointerCount) {
                            2 -> {
                                vibratePhone(50)
                                // Lorsqu'un deuxième doigt touche le bouton
                                // Votre logique ici pour deux doigts
                                true // Indiquer que l'événement est consommé
                            }
                            3 -> {
                                vibratePhone(50)
                                // Lorsqu'un troisième doigt touche le bouton
                                // Votre logique ici pour trois doigts
                                true // Indiquer que l'événement est consommé
                            }
                            else -> {
                                false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                            }
                        }
                    }
                    else -> false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                }
            }
        }



        radioGroup1.setOnCheckedChangeListener { _, checkedId ->
            handleCheckChange(checkedId, radioGroup1, radioGroup2)
            updateButtonState(radioGroup1, radioGroup2, buttonStart)
        }

        radioGroup2.setOnCheckedChangeListener { _, checkedId ->
            handleCheckChange(checkedId, radioGroup2, radioGroup1)
            updateButtonState(radioGroup1, radioGroup2, buttonStart)
        }

        rankedCardView.setOnClickListener {
            val selectedRadioButtonId1 = radioGroup1.checkedRadioButtonId
            val selectedRadioButtonId2 = radioGroup2.checkedRadioButtonId

            if (selectedRadioButtonId1 == -1 && selectedRadioButtonId2 == -1) {
                Toast.makeText(requireContext(), getString(R.string.please_select_option), Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButtonId: Int = when {
                    selectedRadioButtonId1 != -1 -> selectedRadioButtonId1
                    else -> selectedRadioButtonId2
                }

                val idTest = when (selectedRadioButtonId) {
                    R.id.radioButton1 -> {
                        getString(R.string.cent_id)
                    }
                    R.id.radioButton2 -> {
                        getString(R.string.cinq_id)
                    }
                    R.id.radioButton3 -> {
                        getString(R.string.k_id)
                    }
                    R.id.radioButton4 -> {
                        getString(R.string.dix_id)
                    }
                    R.id.radioButton5 -> {
                        getString(R.string.marath_id)
                    }
                    else -> {getString(R.string.career_id)}
                }
                val bundle = Bundle()
                bundle.putString("string1", idTest.toString())
                findNavController().navigate(R.id.action_navigation_home_to_itemFragment, bundle)
            }
        }

        buttonStart.setOnClickListener {
            buttonStart.isEnabled = false
            rankedCardView.visibility = View.GONE
            val selectedRadioButtonId1 = radioGroup1.checkedRadioButtonId
            val selectedRadioButtonId2 = radioGroup2.checkedRadioButtonId

            if (selectedRadioButtonId1 == -1 && selectedRadioButtonId2 == -1) {
                Toast.makeText(requireContext(), getString(R.string.please_select_option), Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButtonId: Int = when {
                    selectedRadioButtonId1 != -1 -> selectedRadioButtonId1
                    else -> selectedRadioButtonId2
                }

                when (selectedRadioButtonId) {
                    R.id.radioButton1 -> {
                        (activity as MainActivity2).setGoal(100)
                    }
                    R.id.radioButton2 -> {
                        (activity as MainActivity2).setGoal(500)
                    }
                    R.id.radioButton3 -> {
                        (activity as MainActivity2).setGoal(1000)
                    }
                    R.id.radioButton4 -> {
                        (activity as MainActivity2).setGoal(10000)
                    }
                    R.id.radioButton5 -> {
                        (activity as MainActivity2).setGoal(42000)
                    }
                }
                countDown(4000)
            }
        }

        val navController = findNavController()

        val isResumeObserver = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("isResume")
        isResumeObserver?.observe(viewLifecycleOwner) { isResume ->
            if (isResume == true) {
                countDown(3000)
            } else {
                // L'utilisateur a arrêté
            }
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_pseudo, menu)
        val maxCharacters = 10
        val surname = (activity as MainActivity2).getSaveName()
        val truncatedSurname = if (surname.length > maxCharacters) {
            surname.substring(0, maxCharacters) + "..."
        } else {
            surname
        }
        if (surname != "User") {
            menu.setGroupVisible(R.id.group_menu_pseudo, true)
            val customActionView = CustomActionView(requireContext())
            customActionView.setTitle(truncatedSurname)
            val menuItem = menu.findItem(R.id.pseudo_visible)
            // Associer la vue personnalisée à l'élément de menu
            MenuItemCompat.setActionView(menuItem, customActionView)
        } else {
            menu.setGroupVisible(R.id.group_menu_pseudo, false)
        }
    }

    private fun updateChronoText() : String {
        val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
        val minutes = (elapsedTime / (1000 * 60)).toInt()
        val seconds = (elapsedTime % (1000 * 60) / 1000).toInt()
        val milliseconds = (elapsedTime % 1000).toInt()

        val formattedTime = String.format("%02d:%02d:%02d.%03d", hours,minutes, seconds, milliseconds)
        chronoTextView.text = formattedTime

        return formattedTime
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer2?.cancel()
    }

    private fun vibratePhone(long: Long) {
        val vibrator = requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(long)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
        countDownTimer2?.cancel()
    }

    private fun handleCheckChange(checkedId: Int, groupToKeep: RadioGroup, otherGroup: RadioGroup) {
        if (checkedId != -1) {
            groupToKeep.setOnCheckedChangeListener(null)
            otherGroup.setOnCheckedChangeListener(null)
            otherGroup.clearCheck()
            groupToKeep.setOnCheckedChangeListener { _, checkedId1 ->
                handleCheckChange(checkedId1, groupToKeep, otherGroup)
            }
            otherGroup.setOnCheckedChangeListener { _, checkedId1 ->
                handleCheckChange(checkedId1, otherGroup, groupToKeep)
            }
        }
    }
    private fun updateButtonState(group1: RadioGroup, group2: RadioGroup, button: Button) {
        val selectedRadioButtonId1 = group1.checkedRadioButtonId
        val selectedRadioButtonId2 = group2.checkedRadioButtonId
        val atLeastOneSelected = selectedRadioButtonId1 != -1 || selectedRadioButtonId2 != -1
        button.isEnabled = atLeastOneSelected
        if (atLeastOneSelected) {
            rankedCardView.visibility = View.VISIBLE
        } else {
            rankedCardView.visibility = View.VISIBLE
        }
    }

    private fun resetAnimCardView() {
        cardViewCircular.visibility = View.VISIBLE
        val anim = ObjectAnimator.ofFloat(cardViewCircular, "translationY", (-cardViewCircular.height - 20).toFloat(), 0f)
        anim.duration = 1000
        anim.start()
    }

    @SuppressLint("SetTextI18n")
    fun clickCounted() {
        val click = (activity as MainActivity2).getNumberClick()
        val number : Int = if (click > 0) {
            click + 1
        } else {
            1
        }
        (activity as MainActivity2).setCounterClick(number)
        val goal2 = (activity as MainActivity2).getGoal()
        textClickNumber.text = "$number/$goal2"

        if (number == goal2) {
            finishPlay(goal2, elapsedTime)
        }
    }

    private fun addSpacesToNumber(number: Int): String {
        val numberString = number.toString()
        val result = StringBuilder()
        var count = 0

        for (i in numberString.length - 1 downTo 0) {
            result.append(numberString[i])
            count++
            if (count % 3 == 0 && i != 0) {
                result.append(" ")
            }
        }

        return result.reverse().toString()
    }

    fun saveRecord(goal: Int) {
        when (goal) {
            500 -> {
                (activity as MainActivity2).setRecord500(elapsedTime)
            }
            1000 -> {
                (activity as MainActivity2).setRecord1k(elapsedTime)
            }
            10000 -> {
                (activity as MainActivity2).setRecord10k(elapsedTime)
            }
            42000 -> {
                (activity as MainActivity2).setRecordMarathon(elapsedTime)
            }
            else -> {
                (activity as MainActivity2).setRecord100(elapsedTime)
            }
        }
    }

    private fun finishPlay(goal: Int, time: Long) {

        val lastRecord = (activity as MainActivity2).findRecords(goal)
//        println(lastRecord)
        var bool = lastRecord > time

        val txt: String
        var recordOne : Long = -1
        val timeValue = elapsedTime
        if (!bool) {
            if (lastRecord <= 0) {
                txt = getString(R.string.click_and_record, addSpacesToNumber(goal), formatDuration(elapsedTime))
                bool = true
            } else {
                txt = getString(R.string.click_and_record, addSpacesToNumber(goal), formatDuration(elapsedTime))
                recordOne = lastRecord
            }
        } else {
            txt = getString(R.string.click_and_record, addSpacesToNumber(goal), formatDuration(elapsedTime))
            recordOne = lastRecord
        }

        saveRecord(goal)
        val surname = (activity as MainActivity2).getSaveName()

        if (surname != "User") {
            if (bool) {
//                saveRecord(goal)
                val database = Firebase.database
                val myRef = database.getReference("utilisateurs")
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val utilisateurData = HashMap<String, Any>().apply {
                            when (goal) {
                                100 -> {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val pseudo = userSnapshot.key ?: ""
                                        val score = userSnapshot.child(getString(R.string.cent_id)).getValue(Int::class.java) ?: 0
                                        if (pseudo != surname) {
                                            if (time < score && score < lastRecord) {
                                                val updates = HashMap<String, Any>()
                                                updates.put("${getString(R.string.cent_id)}_trending", -1)
                                                myRef.child(pseudo).updateChildren(updates)
                                            }
                                        }
                                    }
                                    put("${getString(R.string.cent_id)}_trending", 1)
                                    put(getString(R.string.cent_id) , time)
                                }
                                500 -> {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val pseudo = userSnapshot.key ?: ""
                                        val score = userSnapshot.child(getString(R.string.cinq_id)).getValue(Int::class.java) ?: 0
                                        if (pseudo != surname) {
                                            if (time < score && score < lastRecord) {
                                                val updates = HashMap<String, Any>()
                                                updates.put("${getString(R.string.cinq_id)}_trending", -1)
                                                myRef.child(pseudo).updateChildren(updates)
                                            }
                                        }
                                    }
                                    put("${getString(R.string.cinq_id)}_trending", 1)
                                    put(getString(R.string.cinq_id), time)
                                }
                                1000 -> {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val pseudo = userSnapshot.key ?: ""
                                        val score = userSnapshot.child(getString(R.string.k_id)).getValue(Int::class.java) ?: 0
                                        if (pseudo != surname) {
                                            if (time < score && score < lastRecord) {
                                                val updates = HashMap<String, Any>()
                                                updates.put("${getString(R.string.k_id)}_trending", -1)
                                                myRef.child(pseudo).updateChildren(updates)
                                            }
                                        }
                                    }
                                    put("${getString(R.string.k_id)}_trending", 1)
                                    put(getString(R.string.k_id), time)
                                }
                                10000 -> {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val pseudo = userSnapshot.key ?: ""
                                        val score = userSnapshot.child(getString(R.string.dix_id)).getValue(Int::class.java) ?: 0
                                        if (pseudo != surname) {
                                            if (time < score && score < lastRecord) {
                                                val updates = HashMap<String, Any>()
                                                updates.put("${getString(R.string.dix_id)}_trending", -1)
                                                myRef.child(pseudo).updateChildren(updates)
                                            }
                                        }
                                    }
                                    put("${getString(R.string.dix_id)}_trending", 1)
                                    put(getString(R.string.dix_id), time)
                                }
                                42000 -> {
                                    for (userSnapshot in dataSnapshot.children) {
                                        val pseudo = userSnapshot.key ?: ""
                                        val score = userSnapshot.child(getString(R.string.marath_id)).getValue(Int::class.java) ?: 0
                                        if (pseudo != surname) {
                                            if (time < score && score < lastRecord) {
                                                val updates = HashMap<String, Any>()
                                                updates.put("${getString(R.string.marath_id)}_trending", -1)
                                                myRef.child(pseudo).updateChildren(updates)
                                            }
                                        }
                                    }
                                    put("${getString(R.string.marath_id)}_trending", 1)
                                    put(getString(R.string.marath_id), time)
                                }
                            }
                        }
                        myRef.child(surname).updateChildren(utilisateurData)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //TODO("Not yet implemented")
                    }

                })
            }

            val bravoDialog = BravoDialog(requireContext(), txt, surname, bool, recordOne, elapsedTime)
            contain2.setOnClickListener { }
            countdownTextView.text = ""

            contain2.visibility = View.VISIBLE

            val fadeInAnimation = ObjectAnimator.ofFloat(contain2, "alpha", 0f, 1f)
            fadeInAnimation.duration = 1000
            fadeInAnimation.interpolator = AccelerateInterpolator()

            val fadeInAnimationListener = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)


                    (activity as MainActivity2).putKonfettis()

                    bravoDialog.show()
                    contain2.visibility = View.GONE
                }
            }
// Lancer l'animation de fondu sortant
            fadeInAnimation.addListener(fadeInAnimationListener)
            fadeInAnimation.start()

            // Faire disparaître l'écran noir progressivement
            val fadeOutAnimation = ObjectAnimator.ofFloat(contain2, "alpha", 1f, 0f)
            fadeOutAnimation.duration = 3000
            fadeOutAnimation.interpolator = AccelerateInterpolator()
            fadeOutAnimation.startDelay = 2000

            fadeOutAnimation.start()
        } else {
            binding.buttonClick.isEnabled = false
            val createUserFragment = PseudoDialog(requireContext())
            createUserFragment.isCancelable = false
            createUserFragment.listener = object: PseudoDialog.CreateUserDialogListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onDialogPositiveClick(pseudo: String) {
                    binding.buttonClick.isEnabled = true
                    binding.buttonClick.setOnTouchListener { _, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                vibratePhone(50)
                                // Lorsqu'un doigt touche le bouton
                                // Votre logique ici
                                true // Indiquer que l'événement est consommé
                            }
                            MotionEvent.ACTION_POINTER_DOWN -> {
                                when (event.pointerCount) {
                                    2 -> {
                                        vibratePhone(50)
                                        // Lorsqu'un deuxième doigt touche le bouton
                                        // Votre logique ici pour deux doigts
                                        true // Indiquer que l'événement est consommé
                                    }
                                    3 -> {
                                        vibratePhone(50)
                                        // Lorsqu'un troisième doigt touche le bouton
                                        // Votre logique ici pour trois doigts
                                        true // Indiquer que l'événement est consommé
                                    }
                                    else -> {
                                        false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                                    }
                                }
                            }
                            else -> false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                        }
                    }
                    (activity as MainActivity2).setSaveName(pseudo)
//                    saveRecord(goal)
                    val database = Firebase.database
                    val myRef = database.getReference("utilisateurs")
//                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val utilisateurData = HashMap<String, Any>().apply {
                                when (goal) {
                                    100 -> {
//                                        for (userSnapshot in dataSnapshot.children) {
//                                            val pseudo = userSnapshot.key ?: ""
//                                            val score = userSnapshot.child(getString(R.string.cent_id)).getValue(Int::class.java) ?: 0
//                                            if (time < score && score < lastRecord) {
//                                                val updates = HashMap<String, Any>()
//                                                updates.put("${getString(R.string.cent_id)}_trending", -1)
//                                                myRef.child(pseudo).updateChildren(updates)
//                                            }
//                                        }
                                        put("${getString(R.string.cent_id)}_trending", 1)
                                        put(getString(R.string.cent_id) , time)
                                    }
                                    500 -> {
//                                        for (userSnapshot in dataSnapshot.children) {
//                                            val pseudo = userSnapshot.key ?: ""
//                                            val score = userSnapshot.child(getString(R.string.cinq_id)).getValue(Int::class.java) ?: 0
//                                            if (time < score && score < lastRecord) {
//                                                val updates = HashMap<String, Any>()
//                                                updates.put("${getString(R.string.cinq_id)}_trending", -1)
//                                                myRef.child(pseudo).updateChildren(updates)
//                                            }
//                                        }
                                        put("${getString(R.string.cinq_id)}_trending", 1)
                                        put(getString(R.string.cinq_id), time)
                                    }
                                    1000 -> {
//                                        for (userSnapshot in dataSnapshot.children) {
//                                            val pseudo = userSnapshot.key ?: ""
//                                            val score = userSnapshot.child(getString(R.string.k_id)).getValue(Int::class.java) ?: 0
//                                            if (time < score && score < lastRecord) {
//                                                val updates = HashMap<String, Any>()
//                                                updates.put("${getString(R.string.k_id)}_trending", -1)
//                                                myRef.child(pseudo).updateChildren(updates)
//                                            }
//                                        }
                                        put("${getString(R.string.k_id)}_trending", 1)
                                        put(getString(R.string.k_id), time)
                                    }
                                    10000 -> {
//                                        for (userSnapshot in dataSnapshot.children) {
//                                            val pseudo = userSnapshot.key ?: ""
//                                            val score = userSnapshot.child(getString(R.string.dix_id)).getValue(Int::class.java) ?: 0
//                                            if (time < score && score < lastRecord) {
//                                                val updates = HashMap<String, Any>()
//                                                updates.put("${getString(R.string.dix_id)}_trending", -1)
//                                                myRef.child(pseudo).updateChildren(updates)
//                                            }
//                                        }
                                        put("${getString(R.string.dix_id)}_trending", 1)
                                        put(getString(R.string.dix_id), time)
                                    }
                                    42000 -> {
//                                        for (userSnapshot in dataSnapshot.children) {
//                                            val pseudo = userSnapshot.key ?: ""
//                                            val score = userSnapshot.child(getString(R.string.marath_id)).getValue(Int::class.java) ?: 0
//                                            if (time < score && score < lastRecord) {
//                                                val updates = HashMap<String, Any>()
//                                                updates.put("${getString(R.string.marath_id)}_trending", -1)
//                                                myRef.child(pseudo).updateChildren(updates)
//                                            }
//                                        }
                                        put("${getString(R.string.marath_id)}_trending", 1)
                                        put(getString(R.string.marath_id), time)
                                    }
                                }
                            }
                            myRef.child(pseudo).updateChildren(utilisateurData)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            //TODO("Not yet implemented")
//                        }
//
//                    })
                    val bravoDialog = BravoDialog(requireContext(), txt, pseudo, bool, recordOne, timeValue)
                    contain2.setOnClickListener { }
                    countdownTextView.text = ""

                    contain2.visibility = View.VISIBLE

                    val fadeInAnimation = ObjectAnimator.ofFloat(contain2, "alpha", 0f, 1f)
                    fadeInAnimation.duration = 1000
                    fadeInAnimation.interpolator = AccelerateInterpolator() // Interpolateur pour une transition plus douce
                    val fadeInAnimationListener = object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)

                            (activity as MainActivity2).putKonfettis()
                            bravoDialog.show()
                            contain2.visibility = View.GONE
                        }
                    }
// Lancer l'animation de fondu sortant
                    fadeInAnimation.addListener(fadeInAnimationListener)
                    fadeInAnimation.start()

                    // Faire disparaître l'écran noir progressivement
                    val fadeOutAnimation = ObjectAnimator.ofFloat(contain2, "alpha", 1f, 0f)
                    fadeOutAnimation.duration = 3000
                    fadeOutAnimation.interpolator = AccelerateInterpolator()
                    fadeOutAnimation.startDelay = 2000
                    fadeOutAnimation.start()
                }
            }
            fragmentManager?.let { createUserFragment.show(it, "CreateUserDialogFragment") }
        }

        (activity as MainActivity2).setCounterClick(-1)
        (activity as MainActivity2).setTime(0)
        resetPage()
    }

    private fun countDown(longTime : Long) {
        pauseButton.isEnabled = false
        countDownTimer2 = object : CountDownTimer(longTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val count = (millisUntilFinished / 1000 + 1).toInt()
                val backgroundAlphaAnimator = ObjectAnimator.ofFloat(contain2, "alpha", 0.6f, 1.0f).apply {
                    duration = 1000 // Durée de l'animation en millisecondes
                }
                if (count == 4) {
                    val anim = ObjectAnimator.ofFloat(cardViewCircular, "translationY", 0f, (-cardViewCircular.height - 20).toFloat())
                    anim.duration = 1000 // Durée de l'animation en millisecondes
                    anim.start()
                } else {
                    vibratePhone(80)
                    chronoTextView.visibility = View.VISIBLE
                    textClickNumber.visibility = View.VISIBLE
                    cardViewCircular.visibility = View.GONE
                    pauseButton.visibility = View.VISIBLE
                    contain2.visibility = View.VISIBLE
                    countdownTextView.text = (count).toString()
                    backgroundAlphaAnimator.start()
                }
            }
            @SuppressLint("ClickableViewAccessibility")
            override fun onFinish() {
                vibratePhone(500)
                countDownTimer.start()
                pauseButton.isEnabled = true
                contain2.visibility = View.GONE
                binding.buttonClick.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            vibratePhone(50)
                            clickCounted()
                            // Lorsqu'un doigt touche le bouton
                            // Votre logique ici
                            true // Indiquer que l'événement est consommé
                        }
                        MotionEvent.ACTION_POINTER_DOWN -> {
                            when (event.pointerCount) {
                                2 -> {
                                    vibratePhone(50)
                                    clickCounted()
                                    // Lorsqu'un deuxième doigt touche le bouton
                                    // Votre logique ici pour deux doigts
                                    true // Indiquer que l'événement est consommé
                                }
                                3 -> {
                                    vibratePhone(50)
                                    clickCounted()
                                    // Lorsqu'un troisième doigt touche le bouton
                                    // Votre logique ici pour trois doigts
                                    true // Indiquer que l'événement est consommé
                                }
                                else -> {
                                    false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                                }
                            }
                        }
                        else -> false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                    }
                }
            }
        }.start()
    }

    override fun onResumeClicked() {
        countDown(3000)
    }
    override fun onStopClicked() {
        resetPage()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    fun resetPage() {
        chronoTextView.visibility = View.GONE
        textClickNumber.visibility = View.GONE
        pauseButton.visibility = View.GONE
        textClickNumber.text = "0"
        chronoTextView.text = "0:00:00.000"
        elapsedTime = 0
        countDownTimer.cancel()
        resetAnimCardView()
        binding.buttonClick.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    vibratePhone(50)
                    clickCounted()
                    // Lorsqu'un doigt touche le bouton
                    // Votre logique ici
                    true // Indiquer que l'événement est consommé
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    when (event.pointerCount) {
                        2 -> {
                            vibratePhone(50)
                            clickCounted()
                            // Lorsqu'un deuxième doigt touche le bouton
                            // Votre logique ici pour deux doigts
                            true // Indiquer que l'événement est consommé
                        }
                        3 -> {
                            vibratePhone(50)
                            clickCounted()
                            // Lorsqu'un troisième doigt touche le bouton
                            // Votre logique ici pour trois doigts
                            true // Indiquer que l'événement est consommé
                        }
                        else -> {
                            false // Indiquer que l'événement n'est pas consommé pour d'autres actions
                        }
                    }
                }
                else -> false // Indiquer que l'événement n'est pas consommé pour d'autres actions
            }
        }
        buttonStart.isEnabled = true
        rankedCardView.visibility = View.VISIBLE
    }
    private fun formatDuration(milliseconds: Long): String {
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
        val millis = (milliseconds % 1000).toInt()

        if (milliseconds == 0L) {
            return resources.getString(R.string.seconds, 0)
        }

        val sb = StringBuilder()
        if (hours > 0) {
            sb.append(resources.getString(R.string.hours, hours))
        }
        if (minutes > 0) {
            sb.append(resources.getString(R.string.minutes, minutes))
        }
        if (seconds > 0) {
            sb.append(resources.getString(R.string.seconds, seconds))
        }
        if (millis > 0) {
            sb.append(resources.getString(R.string.milliseconds, millis))
        }

        return sb.toString().trim()
    }
}