package com.zimoliv.buttonrush.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vdurmont.emoji.EmojiManager
import com.zimoliv.buttonrush.MainActivity2
import com.zimoliv.buttonrush.R
import com.zimoliv.buttonrush.databinding.FragmentSettingsBinding
import com.zimoliv.buttonrush.domain.dialog.CountrySelectDialog
import java.util.Locale


class SettingsFragment : Fragment()/*, CountrySelectDialog.CountryDialogListener*/ {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var editStatus = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity2).stopAnimation()
        (activity as MainActivity2).ifClickedOnProfile(true)

        val surname = (activity as MainActivity2).getSaveName()
        if (surname != "User") {
            val database = Firebase.database
            val myRef = database.getReference("utilisateurs")
            binding.titleSet.text = surname
            binding.imageButton.setOnClickListener {
                if (!editStatus) {
                    binding.linearLayout.visibility = View.INVISIBLE
                    binding.editPseudo.visibility = View.VISIBLE
//                binding.editPseudo.setText(surname)
                    binding.imageButton.setImageResource(R.drawable.baseline_check_24)
                    binding.imageButtonCancel.visibility = View.VISIBLE

                    editStatus = true
                } else {
                    val pseudo = binding.editPseudo.text.toString()
                    if (pseudo.isNotEmpty()) {
                        if (pseudo.length < 20) {

                            myRef.child(pseudo).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(requireContext(), getString(R.string.alredy_taken_pseudo), Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Le nouveau pseudo est disponible, procéder au changement
                                        myRef.child(surname).get()
                                            .addOnSuccessListener { currentUserSnapshot ->
                                                if (currentUserSnapshot.exists()) {
                                                    val userData = currentUserSnapshot.value
                                                    // Supprimer l'ancien pseudo
                                                    myRef.child(surname).removeValue()
                                                        .addOnSuccessListener {
                                                            // Ajouter les données sous le nouveau pseudo
                                                            myRef.child(pseudo)
                                                                .setValue(userData)
                                                                .addOnSuccessListener {
                                                                    Toast.makeText(
                                                                        context,
                                                                        getString(R.string.pseudo_chang_avec_succ_s),
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    changedSucessfully(pseudo, requireContext())
                                                                }.addOnFailureListener {
                                                                // Échec de l'ajout des données sous le nouveau pseudo
                                                                Toast.makeText(
                                                                    context,
                                                                    getString(R.string.chec_du_changement_de_pseudo),
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }.addOnFailureListener {
                                                        // Échec de la suppression de l'ancien pseudo
                                                        Toast.makeText(
                                                            context,
                                                            getString(R.string.chec_de_la_suppression_de_l_ancien_pseudo),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                } else {
                                                    // L'utilisateur avec le pseudo actuel n'existe pas
                                                    Toast.makeText(
                                                        context,
                                                        getString(R.string.l_utilisateur_n_existe_pas),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }.addOnFailureListener {
                                            // Échec de la récupération des données de l'utilisateur actuel
                                            Toast.makeText(
                                                context,
                                                getString(R.string.chec_de_la_r_cup_ration_des_donn_es_de_l_utilisateur),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(requireContext(),
                                        getString(R.string.n_cessite_une_connection_internet), Toast.LENGTH_LONG).show()
                                }
                            })
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.twenty_minimum), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.write_your_surname), Toast.LENGTH_LONG).show()
                    }
                }
            }

            binding.imageButtonCancel.setOnClickListener {
                finishEditing()
            }

            val country = (activity as MainActivity2).getCountry()
            putFlag(country)

            binding.editFlagButton.setOnClickListener {
                val locale = Locale("", country)
                val countryName = locale.displayCountry
                val dialog = CountrySelectDialog(countryName)
                dialog.listener = object : CountrySelectDialog.CountryDialogListener {
                    override fun onCountrySelected(country: String) {
                        (activity as MainActivity2).setCountry(country)
                        val utilisateurData = HashMap<String, Any>().apply {
                            put("country", country)
                        }
                        myRef.child(surname).setValue(utilisateurData)
                        putFlag(country)
                    }

                }
                dialog.show(parentFragmentManager, "CountrySelectDialog")
            }
            setSettings()
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.une_erreur_est_survenue_r_essayer_plus_tard), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        setHasOptionsMenu(true)
        return root
    }

    private fun setSettings() {
        val activity2 =  (activity as MainActivity2)

        binding.click100.text = ": ${formatTime(activity2.findRecords(100).toInt())}"
        binding.click500.text = ": ${formatTime(activity2.findRecords(500).toInt())}"
        binding.click1k.text = ": ${formatTime(activity2.findRecords(1000).toInt())}"
        binding.click10k.text = ": ${formatTime(activity2.findRecords(10000).toInt())}"
        binding.click42k.text = ": ${formatTime(activity2.findRecords(42000).toInt())}"

        binding.friensAndNumber.text = getString(R.string.friends, (activity2.getSaveFriends().size - 1).toString())

        binding.textView2.text = formatIntWithSpaces(activity2.getSaveNumber())
    }

    private fun formatIntWithSpaces(number: Int): String {
        return "%,d".format(number).replace(",", " ")
    }

    private fun formatTime(milliseconds: Int): String {
        if (milliseconds == 0) {
            return "--:--"
        }

        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60))
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000)
        val millis = (milliseconds % 1000)

        val formattedTime = StringBuilder()

        if (hours > 0) {
            formattedTime.append("$hours h ")
        }
        if (minutes > 0) {
            formattedTime.append("$minutes min ")
        }
        if (seconds > 0) {
            formattedTime.append("$seconds s ")
        }
        if (millis > 0) {
            formattedTime.append("$millis ms ")
        }

        return formattedTime.toString().trim()
    }

    private fun putFlag(country: String?) {
        val emoji = EmojiManager.getForAlias(country?.lowercase(Locale.getDefault()) ?: "")
        binding.flagUnicodeText.text = emoji.unicode
    }

    private fun finishEditing() {
        binding.linearLayout.visibility = View.VISIBLE
        binding.editPseudo.visibility = View.GONE
//                binding.editPseudo.setText(surname)
        binding.imageButton.setImageResource(R.drawable.baseline_edit_square_24)
        binding.imageButtonCancel.visibility = View.GONE

        editStatus = false
    }

    private fun changedSucessfully(pseudo: String, ctxt: Context) {
        (activity as MainActivity2).setSaveName(pseudo)
        closeKeyboard(ctxt, requireView())
        binding.titleSet.text = pseudo
        finishEditing()
    }

    private fun closeKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}