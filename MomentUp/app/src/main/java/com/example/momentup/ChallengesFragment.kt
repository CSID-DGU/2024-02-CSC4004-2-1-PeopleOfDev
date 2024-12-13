package com.example.momentup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.momentup.databinding.FragmentChallengesBinding

class ChallengesFragment : Fragment() {

    private var _binding: FragmentChallengesBinding? = null
    private val binding get() = _binding!!
    private lateinit var challengesAdapter: ChallengesAdapter

    data class Challenge(
        val id: String,
        val name: String,
        val description: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        loadChallenges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        challengesAdapter = ChallengesAdapter { challenge ->
            // 챌린지 클릭 처리
        }
        binding.recyclerViewChallenges.apply {
            adapter = challengesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadChallenges()
        }
    }

    private fun loadChallenges() {
        // 임시 데이터
        val challenges = listOf(
            Challenge("1", "7일 챌린지", "김동국의 STUDY"),
            Challenge("2", "30일 챌린지", "홍길동의 운동일기"),
            Challenge("3", "한달 챌린지", "박종상의 모닝루틴"),
            Challenge("4", "일년 챌린지", "이민지의 독서생활"),
            Challenge("5", "일주일 챌린지", "최영희의 요리일기")
        )

        binding.progressBar.visibility = View.GONE
        binding.swipeRefreshLayout.isRefreshing = false

        if (challenges.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerViewChallenges.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerViewChallenges.visibility = View.VISIBLE
            challengesAdapter.submitList(challenges)
        }
    }

    private class ChallengesAdapter(
        private val onClick: (Challenge) -> Unit
    ) : ListAdapter<Challenge, ChallengesAdapter.ViewHolder>(ChallengeDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_challenge, parent, false)
            return ViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class ViewHolder(
            view: View,
            private val onClick: (Challenge) -> Unit
        ) : RecyclerView.ViewHolder(view) {
            private val nameView: TextView = view.findViewById(R.id.challengeName)
            private val descriptionView: TextView = view.findViewById(R.id.challengeDescription)
            private var currentChallenge: Challenge? = null

            init {
                view.setOnClickListener {
                    currentChallenge?.let {
                        onClick(it)
                    }
                }
            }

            fun bind(challenge: Challenge) {
                currentChallenge = challenge
                nameView.text = challenge.name
                descriptionView.text = challenge.description
            }
        }
    }

    private class ChallengeDiffCallback : DiffUtil.ItemCallback<Challenge>() {
        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem == newItem
        }
    }
}