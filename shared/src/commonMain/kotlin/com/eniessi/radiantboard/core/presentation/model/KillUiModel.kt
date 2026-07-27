package com.eniessi.radiantboard.core.presentation.model

import com.eniessi.radiantboard.core.domain.Kill
import com.eniessi.radiantboard.core.domain.MatchPlayerInfo
import com.eniessi.radiantboard.core.domain.RelativePosition

data class KillUiModel(
    val kill: Kill,
    val victimRelPosition: RelativePosition,
    val killerRelPosition: RelativePosition?,
    val victimAgentInfo: MatchPlayerInfo?,
    val killerAgentInfo: MatchPlayerInfo?,
    val isVictimCurrentUser: Boolean,
    val isKillerCurrentUser: Boolean,
    val isAlly: Boolean,
    val isKillerAlly: Boolean,
    val isVectorActive: Boolean
)
