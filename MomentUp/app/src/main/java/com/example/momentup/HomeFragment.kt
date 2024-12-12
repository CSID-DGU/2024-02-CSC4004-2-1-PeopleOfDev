package com.example.momentup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.momentup.databinding.FragmentHomeBinding
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.momentup.model.Group

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var groupAdapter: GroupAdapter
    private lateinit var groupRepository: GroupRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGroupList()
        setupClickListeners()
        loadGroups()
    }

    private fun setupGroupList() {
        groupAdapter = GroupAdapter()
        groupAdapter.setOnGroupClickListener { group ->
            val intent = Intent(requireContext(), GroupDetailActivity::class.java).apply {
                putExtra("groupNumber", group.getGroupNumber())
            }
            startActivity(intent)
        }

        binding.recyclerViewGroups.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupClickListeners() {
        binding.btnAddGroup.setOnClickListener {
            val intent = Intent(requireContext(), GroupCreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadGroups() {
        groupRepository = GroupRepository.create(requireContext())

        groupRepository.getGroup(1L, object : GroupRepository.GroupCallback {
            override fun onSuccess(group: Group) {
                activity?.runOnUiThread {
                    groupAdapter.setGroup(group)
                }
            }

            override fun onFailure(throwable: Throwable) {
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "그룹을 불러오는데 실패했습니다: ${throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}