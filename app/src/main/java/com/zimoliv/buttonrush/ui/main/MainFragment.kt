package com.zimoliv.buttonrush.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zimoliv.buttonrush.domain.CustomActionView
import com.zimoliv.buttonrush.domain.LeaderboardManager
import com.zimoliv.buttonrush.MainActivity2
import com.zimoliv.buttonrush.domain.dialog.PseudoDialog
import com.zimoliv.buttonrush.R
import com.zimoliv.buttonrush.database.data.MutableInt
import com.zimoliv.buttonrush.databinding.FragmentMainBinding
import com.zimoliv.buttonrush.ui.ranked.UserItem

class MainFragment : Fragment() {


    private val handler2 = Handler()
    private var clickCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private var listUser: MutableList<UserItem> = mutableListOf()
    private var pointsDuJoueurDevant = -1
    private var _binding: FragmentMainBinding? = null
    private lateinit var viewModel: MainViewModel
    private val binding get() = _binding!!
    private lateinit var tutorialBubble: ConstraintLayout
    private var actualNumberProgress = MutableInt(0)
    private lateinit var adapter: ProgressButtonsRecyclerViewAdapter
    private var lastValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tutorialBubble = binding.tutorialBubble
        val tutorialMessage = binding.tutorialMessage

        adapter = ProgressButtonsRecyclerViewAdapter(actualNumberProgress, requireContext())
        val recyclerView = binding.listProgress
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        if (viewModel.number.value == -1 || viewModel.number.value == null) {
            val actualNumber = (activity as MainActivity2).getSaveNumber()
            viewModel.number.value = actualNumber
            if (actualNumber in 1001..2499) {
                val color = ContextCompat.getColor(requireContext(), R.color.yellow)

                binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
            if (actualNumber < 1) {
                tutorialMessage.text = requireContext().getString(R.string.welcome_message)
                binding.numberGoal.text = requireContext().getString(R.string.goal_10)
                showTutorialBubble()

                tutorialBubble.setOnClickListener {
                    hideTutorialBubble()
                }
            }
            if (actualNumber > 300) {
                getUserFirebase()
            }

            if (actualNumber > 500) {
                actualNumberProgress.value = actualNumber
                adapter.notifyDataSetChanged()
            }
        }

        binding.constraintProgressBar.setOnClickListener {
            binding.listProgress.visibility = View.VISIBLE
            binding.constraintProgressBar.visibility = View.INVISIBLE
            goToPosition()
        }

        viewModel.textProgress.observe(viewLifecycleOwner) { it ->

            if (it == (activity as MainActivity2).getSaveNumber()) {
                binding.saveButton.isEnabled = false
                binding.saveButton.alpha = 0.4f
                (activity as MainActivity2).setNavigationEnabled(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.saveButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_one))
                }
            } else {
                binding.saveButton.isEnabled = true
                binding.saveButton.alpha = 1f
                if (it > 150) {
                    (activity as MainActivity2).setNavigationEnabled(false)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.saveButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.adapter_red))
                }
            }

            binding.message.text = formatNumberWithSpaces(it)

            when {
                it < 10 -> binding.numberGoal.text = requireContext().getString(R.string.goal_10)
                it < 100 -> binding.numberGoal.text = requireContext().getString(R.string.goal_100)
                it < 150 -> binding.numberGoal.text = requireContext().getString(R.string.goal_150)
                it < 300 -> binding.numberGoal.text = requireContext().getString(R.string.goal_300)
                it < 500 -> binding.numberGoal.text = requireContext().getString(R.string.goal_500)
                it < 1000 -> binding.numberGoal.text = requireContext().getString(R.string.button_1k)
                it < 2500 -> binding.numberGoal.text = requireContext().getString(R.string.button_2_5k)
                it < 5000 -> binding.numberGoal.text = requireContext().getString(R.string.button_5k)
                it < 10000 -> binding.numberGoal.text = requireContext().getString(R.string.button_10k)
                it < 15000 -> binding.numberGoal.text = requireContext().getString(R.string.button_15k)
                it < 20000 -> binding.numberGoal.text = requireContext().getString(R.string.button_20k)
                it < 30000 -> binding.numberGoal.text = requireContext().getString(R.string.button_30k)
                it < 50000 -> binding.numberGoal.text = requireContext().getString(R.string.button_50k)
                it < 100000 -> binding.numberGoal.text = requireContext().getString(R.string.button_100k)
                else -> binding.numberGoal.text = requireContext().getString(R.string.goal_million)
            }

            if (it > 500) {
                binding.trophyButton.visibility = View.VISIBLE
                binding.constraintProgressBar.visibility = View.VISIBLE
                binding.listProgress.visibility = View.GONE
                actualNumberProgress.value = it
                adapter.notifyDataSetChanged()
                setPogressBar(it)
            }

            if (it == 300) {
                val surname = (activity as MainActivity2).getSaveName()
                val database = Firebase.database
                val myRef = database.getReference("utilisateurs")
                val countriesRef = database.getReference("countries")
//                val utilisateurData = HashMap<String, Any>().apply {
//                    put(getString(R.string.career_id), 301)
//                }

                if (surname != "User") {
                    val utilisateurData = HashMap<String, Any>().apply {
                        put(getString(R.string.career_id), 301)
                    }
                    myRef.child(surname).setValue(utilisateurData)

                    val countr = (activity as MainActivity2).getCountry()
                    if (countr != null) {
                        countriesRef.child(countr).runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val currentValue = currentData.getValue(Int::class.java) ?: 0
                                currentData.value = currentValue + 301
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                // Transaction completed
                                if (error != null) {
                                    println("Transaction failed: ${error.message}")
//                                    (activity as MainActivity2).lastValueUpdated(301)

                                } else {
                                    println("Transaction success")
                                }
                            }
                        })
                    }
                } else {
                    val createUserFragment = PseudoDialog(requireContext())
                    createUserFragment.isCancelable = false
                    createUserFragment.listener = object: PseudoDialog.CreateUserDialogListener {
                        override fun onDialogPositiveClick(pseudo: String, country: String) {
                            (activity as MainActivity2).setSaveName(pseudo)
                            (activity as MainActivity2).setCountry(country)
                            val utilisateurData = HashMap<String, Any>().apply {
                                put(getString(R.string.career_id), 301)
                                put("country", country)
                            }
                            myRef.child(pseudo).setValue(utilisateurData)
                            countriesRef.child(country).runTransaction(object : Transaction.Handler {
                                override fun doTransaction(currentData: MutableData): Transaction.Result {
                                    val currentValue = currentData.getValue(Int::class.java) ?: 0
                                    currentData.value = currentValue + 301
                                    return Transaction.success(currentData)
                                }

                                override fun onComplete(
                                    error: DatabaseError?,
                                    committed: Boolean,
                                    currentData: DataSnapshot?
                                ) {
                                    // Transaction completed
                                    if (error != null) {
                                        println("Transaction failed: ${error.message}")
//                                        (activity as MainActivity2).lastValueUpdated(301)

                                    } else {
                                        println("Transaction success")
                                    }
                                }
                            })
                            viewModel.number.value?.let { it1 ->
                                (activity as MainActivity2).setSaveNumber(
                                    it1 + 1
                                )
                            }
                        }
                    }
                    fragmentManager?.let { createUserFragment.show(it, "CreateUserDialogFragment") }
                }


            }

            if (it == 10) {
                tutorialMessage.text = resources.getString(R.string.ugly_button_message)
                showTutorialBubble()
                tutorialBubble.setOnClickListener {
                    hideTutorialBubble()
                }
            }

            if (it == 100) {
                tutorialMessage.text = resources.getString(R.string.aaa_voila_la)
                showTutorialBubble()

                tutorialBubble.setOnClickListener {
                    hideTutorialBubble()
                }
            }

            if (it == 150) {
                binding.saveButton.visibility = View.VISIBLE
                tutorialMessage.text = resources.getString(R.string.new_button_message)
                showTutorialBubble()

                binding.darkOverlay.visibility = View.VISIBLE

                tutorialBubble.isEnabled = false
                binding.buttonId.isEnabled = false
            }

            if (it < 150) {
                (activity as MainActivity2).setSaveNumber(it)
                binding.verticalProgressbar.visibility = View.GONE
                binding.numberClickSeconde.visibility = View.GONE
                binding.saveButton.visibility = View.GONE
            } else {
                binding.verticalProgressbar.visibility = View.VISIBLE
                binding.numberClickSeconde.visibility = View.VISIBLE
                binding.saveButton.visibility = View.VISIBLE
            }

            if (it < 100 && it > -1) {
                binding.buttonId.setImageResource(R.drawable.button_png)
                binding.buttonId.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            viewModel.number.value = viewModel.number.value?.plus(1)

                            vibratePhone(50)
                            // Lorsqu'un doigt touche le bouton
                            // Votre logique ici
                            true // Indiquer que l'événement est consommé
                        }
                        MotionEvent.ACTION_POINTER_DOWN -> {
                            when (event.pointerCount) {
                                2 -> {
                                    viewModel.number.value = viewModel.number.value?.plus(1)

                                    vibratePhone(50)
                                    // Lorsqu'un deuxième doigt touche le bouton
                                    // Votre logique ici pour deux doigts
                                    true // Indiquer que l'événement est consommé
                                }
                                3 -> {
                                    viewModel.number.value = viewModel.number.value?.plus(1)

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
            } else if (it < 1000) {
                binding.buttonId.setImageResource(R.drawable.click_button_2_clicked)
                binding.buttonId.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            clickCount++
                            viewModel.number.value = viewModel.number.value?.plus(1)
                            vibratePhone(50)
                            binding.buttonId.setImageResource(R.drawable.click_button_2)
                            Handler().postDelayed({
                                binding.buttonId.setImageResource(R.drawable.click_button_2_clicked)
                            }, 20)
                            // Lorsqu'un doigt touche le bouton
                            // Votre logique ici
                            true // Indiquer que l'événement est consommé
                        }
                        MotionEvent.ACTION_POINTER_DOWN -> {
                            when (event.pointerCount) {
                                2 -> {
                                    clickCount++
                                    viewModel.number.value = viewModel.number.value?.plus(1)
                                    vibratePhone(50)
                                    binding.buttonId.setImageResource(R.drawable.click_button_2)
                                    Handler().postDelayed({
                                        binding.buttonId.setImageResource(R.drawable.click_button_2_clicked)
                                    }, 20)
                                    // Lorsqu'un deuxième doigt touche le bouton
                                    // Votre logique ici pour deux doigts
                                    true // Indiquer que l'événement est consommé
                                }
                                3 -> {
                                    clickCount++
                                    viewModel.number.value = viewModel.number.value?.plus(1)
                                    vibratePhone(50)
                                    binding.buttonId.setImageResource(R.drawable.click_button_2)
                                    Handler().postDelayed({
                                        binding.buttonId.setImageResource(R.drawable.click_button_2_clicked)
                                    }, 20)
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
            } else {
                binding.clickAnimationView.visibility = View.VISIBLE
                binding.buttonId.setOnTouchListener { _, event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            clickCount++
                            viewModel.number.value = viewModel.number.value?.plus(1)
                            vibratePhone(50)
                            // Lorsqu'un doigt touche le bouton
                            // Votre logique ici
                            true // Indiquer que l'événement est consommé
                        }
                        MotionEvent.ACTION_POINTER_DOWN -> {
                            when (event.pointerCount) {
                                2 -> {
                                    clickCount++
                                    viewModel.number.value = viewModel.number.value?.plus(1)
                                    vibratePhone(50)
                                    // Lorsqu'un deuxième doigt touche le bouton
                                    // Votre logique ici pour deux doigts
                                    true // Indiquer que l'événement est consommé
                                }
                                3 -> {
                                    clickCount++
                                    viewModel.number.value = viewModel.number.value?.plus(1)
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

                if (it < 2500) {
                    binding.buttonId.setImageResource(R.drawable.click_3)
                } else if (it < 5000) {
                    binding.buttonId.setImageResource(R.drawable.button_two_five_k)
                } else if (it < 10000) {
                    binding.buttonId.setImageResource(R.drawable.fire_button)
                } else if (it < 15000) {
                    binding.buttonId.setImageResource(R.drawable.blue_dark_knight)
                } else if (it < 20000) {
                    binding.buttonId.setImageResource(R.drawable.earth_button_one)
                } else if (it < 30000) {
                    binding.buttonId.setImageResource(R.drawable.earth_two)
                } else if (it < 50000) {
                    binding.buttonId.setImageResource(R.drawable.knight_one)
                } else if (it < 100000) {
                    binding.buttonId.setImageResource(R.drawable.knight_two)
                } else {
                    binding.buttonId.setImageResource(R.drawable.gold_sword)
                }
            }
        }

        binding.saveButton.setOnClickListener {
            vibratePhone(400)

            viewModel.number.value?.let { it1 ->

                if (it1 > 299) {
                    val newData = viewModel.number.value.toString().toInt()
                    val lastData = (activity as MainActivity2).getSaveNumber()
                    val database = Firebase.database
                    val myRef = database.getReference("utilisateurs")
                    val utilisateurData = HashMap<String, Any>().apply {
                        put(getString(R.string.career_id), newData)
                        put("${getString(R.string.career_id)}_trending", 1)
                    }
                    val surname = (activity as MainActivity2).getSaveName()
                    val countr = (activity as MainActivity2).getCountry()
                    val countriesRef = database.getReference("countries")
                    if (countr != null) {
                        val number = newData - (activity as MainActivity2).getSaveNumber()

                        countriesRef.child(countr).runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val currentValue = currentData.getValue(Int::class.java) ?: 0
                                currentData.value = currentValue + number
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                // Transaction completed
                                if (error != null) {
                                    println("Transaction failed: ${error.message}")
//                                    (activity as MainActivity2).lastValueUpdated(301)

                                } else {
                                    println("Transaction success")
                                }
                            }
                        })
                    }
                    myRef.child(surname).updateChildren(utilisateurData)
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children) {
                                val pseudo = userSnapshot.key ?: ""
                                val score = userSnapshot.child(getString(R.string.career_id)).getValue(Int::class.java) ?: 0

                                if (pseudo != surname) {
                                    if (score in (lastData + 1)..<newData) {
                                        val updates = HashMap<String, Any>()
                                        updates["${getString(R.string.career_id)}_trending"] = -1
                                        myRef.child(pseudo).updateChildren(updates)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //TODO("Not yet implemented")
                        }

                    })
                }

                if (it1 == 150) {
                    (activity as MainActivity2).setSaveNumber(it1 + 1)
                    tutorialMessage.text = resources.getString(R.string.bravo_three_hundred_goal_now)
                    showTutorialBubble()
                    binding.saveButton.visibility = View.GONE

                    binding.darkOverlay.setOnClickListener {
                        binding.darkOverlay.visibility = View.GONE
                        tutorialBubble.isEnabled = true
                        binding.buttonId.isEnabled = true
                        binding.numberGoal.text = resources.getString(R.string.goal_300)
                        hideTutorialBubble()
                        binding.saveButton.visibility = View.VISIBLE
                    }

                    tutorialBubble.setOnClickListener {
                        binding.darkOverlay.visibility = View.GONE
                        tutorialBubble.isEnabled = true
                        binding.buttonId.isEnabled = true
                        binding.numberGoal.text = resources.getString(R.string.goal_300)
                        hideTutorialBubble()
                        binding.saveButton.visibility = View.VISIBLE
                    }
                } else {
                    (activity as MainActivity2).setSaveNumber(it1)
                }
            }

            binding.saveButton.isEnabled = false
            binding.saveButton.alpha = 0.4f
            (activity as MainActivity2).setNavigationEnabled(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_one))
            }
        }

//        startCPSCheck()
        handler.removeCallbacksAndMessages(null)
        startPeriodicTask()

        binding.trophyButton.setOnClickListener {

            if (viewModel.number.value != (activity as MainActivity2).getSaveNumber()) {
                Toast.makeText(requireContext(), getString(R.string.save_before), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle()
            bundle.putString("string1", getString(R.string.career_id))
            findNavController().navigate(R.id.action_navigation_dashboard_to_itemFragment, bundle)
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

    private fun setPogressBar(number: Int) {
        if (number < 1000) {
            binding.leftImage.setImageResource(R.drawable.click_button_2_clicked)
            binding.rightImage.setImageResource(R.drawable.click_3)
            binding.progressBarClick.max = 1000
            binding.textViewProgress.text = "$number/1000"
        } else if (number < 2500) {
            binding.leftImage.setImageResource(R.drawable.click_3)
            binding.rightImage.setImageResource(R.drawable.button_two_five_k)
            binding.progressBarClick.max = 2500
            binding.textViewProgress.text = "$number/2500"
        } else if (number < 5000) {
            binding.leftImage.setImageResource(R.drawable.button_two_five_k)
            binding.rightImage.setImageResource(R.drawable.fire_button)
            binding.progressBarClick.max = 5000
            binding.textViewProgress.text = "$number/5000"
        } else if (number < 10000) {
            binding.leftImage.setImageResource(R.drawable.fire_button)
            binding.rightImage.setImageResource(R.drawable.blue_dark_knight)
            binding.progressBarClick.max = 10000
            binding.textViewProgress.text = "$number/10000"
        } else if (number < 15000) {
            binding.leftImage.setImageResource(R.drawable.blue_dark_knight)
            binding.rightImage.setImageResource(R.drawable.earth_button_one)
            binding.progressBarClick.max = 15000
            binding.textViewProgress.text = "$number/15000"
        } else if (number < 20000) {
            binding.leftImage.setImageResource(R.drawable.earth_button_one)
            binding.rightImage.setImageResource(R.drawable.earth_two)
            binding.progressBarClick.max = 20000
            binding.textViewProgress.text = "$number/20000"
        } else if (number < 30000) {
            binding.leftImage.setImageResource(R.drawable.earth_two)
            binding.rightImage.setImageResource(R.drawable.knight_one)
            binding.progressBarClick.max = 30000
            binding.textViewProgress.text = "$number/30000"
        } else if (number < 50000) {
            binding.leftImage.setImageResource(R.drawable.knight_one)
            binding.rightImage.setImageResource(R.drawable.knight_two)
            binding.progressBarClick.max = 50000
            binding.textViewProgress.text = "$number/50000"
        } else if (number < 100000) {
            binding.leftImage.setImageResource(R.drawable.knight_two)
            binding.rightImage.setImageResource(R.drawable.gold_sword)
            binding.progressBarClick.max = 100000
            binding.textViewProgress.text = "$number/100000"
        } else {
            binding.leftImage.setImageResource(R.drawable.gold_sword)
            binding.rightImage.setImageResource(R.drawable.trophy_small)
            binding.progressBarClick.max = 1000
            val lastThreeDigits = number % 1000
            binding.progressBarClick.progress = lastThreeDigits

            val nextThousand = (number / 1000 + 1) * 1000
            binding.textViewProgress.text = "$number/$nextThousand"
        }
        binding.progressBarClick.progress = number

        val targetImageView = binding.imageViewSparkle

        if (number == 1000
            || number == 2500
            || number == 5000
            || number == 10000
            || number == 15000
            || number == 20000
            || number == 30000
            || number == 50000
            || number == 100000) {
            // Assurez-vous que l'ImageView est initialement invisible
            targetImageView.visibility = View.INVISIBLE
// Créez une animation pour faire apparaître l'ImageView en augmentant sa transparence
            val fadeInAnimator = ObjectAnimator.ofFloat(targetImageView, View.ALPHA, 0f, 1f)
            fadeInAnimator.duration = 400 // Durée de l'animation en millisecondes
// Créez une animation pour faire disparaître l'ImageView en diminuant sa transparence
            val fadeOutAnimator = ObjectAnimator.ofFloat(targetImageView, View.ALPHA, 1f, 0f)
            fadeOutAnimator.duration = 700 // Durée de l'animation en millisecondes
// Créez un ensemble d'animations pour exécuter les deux animations séquentiellement
            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(fadeInAnimator, fadeOutAnimator)
// Définissez un écouteur pour réinitialiser la visibilité de l'ImageView à la fin de l'animation
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    targetImageView.visibility = View.INVISIBLE
                }
            })
// Affichez l'ImageView et exécutez l'ensemble d'animations
            targetImageView.visibility = View.VISIBLE
            animatorSet.start()
        }
    }

    private fun startPeriodicTask() {
        handler2.post(object : Runnable {
            override fun run() {
                val cps = clickCount
                clickCount = 0
                binding.numberClickSeconde.text = cps.toString()

                if (cps > 14) {
                    animateProgressBar(lastValue, cps, 500, true)
                    binding.verticalProgressbar.progress = 0
                } else {
                    animateProgressBar(lastValue, cps, 500, false)
                    binding.verticalProgressbar.secondaryProgress  = 0
                }

                try {
                    if (viewModel.number.value!! in 1001..2499) {
                        if (cps < 4) {
                            val color = ContextCompat.getColor(requireContext(), R.color.yellow)
                            binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                        } else if (cps < 8) {
                            val color = ContextCompat.getColor(requireContext(), R.color.orange)
                            binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                        }  else if (cps < 12) {
                            val color = ContextCompat.getColor(requireContext(), R.color.red_orange)
                            binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                        } else {
                            val color = ContextCompat.getColor(requireContext(), R.color.red)
                            binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                        }
                    } else {
                        val transparentColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
                        binding.buttonId.setColorFilter(transparentColor, PorterDuff.Mode.SRC_ATOP)
                    }
                } catch (_: Exception) {  }

                lastValue = cps
                handler.postDelayed(this, 1000)
            }
        })
    }

//    private fun startCPSCheck() {
//        handler.postDelayed({
//
//            val cps = clickCount
//            clickCount = 0
//            binding.numberClickSeconde.text = cps.toString()
//
//            if (cps > 14) {
////                binding.verticalProgressbar.secondaryProgress  = cps
//                animateProgressBar(lastValue, cps, 500, true)
//                binding.verticalProgressbar.progress = 0
//            } else {
////                binding.verticalProgressbar.progress = cps
//                animateProgressBar(lastValue, cps, 500, false)
//                binding.verticalProgressbar.secondaryProgress  = 0
//            }
//
//            try {
//                if (viewModel.number.value!! in 1001..2499) {
//                    if (cps < 4) {
//
//                        val color = ContextCompat.getColor(requireContext(), R.color.yellow)
//                        binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
//                    } else if (cps < 8) {
//
//                        val color = ContextCompat.getColor(requireContext(), R.color.orange)
//                        binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
//                    }  else if (cps < 12) {
//
//                        val color = ContextCompat.getColor(requireContext(), R.color.red_orange)
//                        binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
//                    } else {
//
//                        val color = ContextCompat.getColor(requireContext(), R.color.red)
//                        binding.buttonId.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
//                    }
//                } else {
//                    val transparentColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
//                    binding.buttonId.setColorFilter(transparentColor, PorterDuff.Mode.SRC_ATOP)
//
//                }
//            } catch (_: Exception) {  }
//
//            lastValue = cps
//            startCPSCheck()
//        }, 1000)
//    }

    private fun animateProgressBar(startProgress: Int, endProgress: Int, duration: Long, secondary: Boolean) {
        val progressBar = binding.verticalProgressbar
        val animator = ValueAnimator.ofInt(startProgress * 1000, endProgress * 1000)
        animator.addUpdateListener { valueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            if (secondary) {
                progressBar.secondaryProgress = progress
            } else {
                progressBar.progress = progress
            }
        }
        animator.duration = duration
        animator.start()
    }

    private fun vibratePhone(long: Long) {
        val vibrator = requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(long)
    }

    private fun showTutorialBubble() {
        tutorialBubble.visibility = View.VISIBLE
        binding.darkOverlay.visibility = View.VISIBLE
        binding.buttonId.isEnabled = false
    }

    private fun hideTutorialBubble() {
        tutorialBubble.visibility = View.GONE
        binding.darkOverlay.visibility = View.GONE
        binding.buttonId.isEnabled = true
    }

    private fun formatNumberWithSpaces(number: Int): String {
        val numberString = number.toString()
        val length = numberString.length

        val result = StringBuilder()
        for (i in 0 until length) {
            result.append(numberString[i])
            if ((length - i) % 3 == 1 && i < length - 1) {
                result.append(' ')
            }
        }

        return result.toString()
    }

    private fun getUserFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        val leaderboardManager = LeaderboardManager(database)

        leaderboardManager.getLeaderboard(getString(R.string.career_id)) { leaderboard ->
            listUser.clear()

//            leaderboard.forEach { (username, score) ->
//                val userData = UserItem(username, score)
//                listUser.add(userData)
//            }
            listUser.addAll(leaderboard)

            listUser.sortByDescending { it.number }
            getNextScore()
        }
    }

    private fun getNextScore() {

        val user = (activity as MainActivity2).getSaveName()
        val positionToUser = listUser.indexOfFirst { it.pseudo == user }

        if (positionToUser > 0 && positionToUser < listUser.size) {
            pointsDuJoueurDevant = listUser[positionToUser - 1].number
            binding.numberGoal.text = getString(R.string.objectif_classement, pointsDuJoueurDevant)
        }
    }

    private fun goToPosition() {
        val targetProgress = actualNumberProgress.value
        val recyclerView: RecyclerView = binding.listProgress

        val position = when {
            targetProgress < 100 -> 1
            targetProgress < 1000 -> 2
            targetProgress < 2500 -> 3
            targetProgress < 5000 -> 4
            targetProgress < 10000 -> 5
            targetProgress < 15000 -> 6
            targetProgress < 20000 -> 7
            targetProgress < 30000 -> 8
            targetProgress < 50000 -> 9
            targetProgress < 100000 -> 10
            else -> 11
        }

        recyclerView.scrollToPosition(position)
    }
}