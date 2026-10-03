package com.currupt.reflame.feature.admin

import androidx.compose.runtime.Composable
import com.currupt.reflame.core.model.ContentType

@Composable
fun AnnouncementManagementScreen(
    onBackClick: () -> Unit,
    onEditAnnouncement: (String?) -> Unit
) {
    // Reusing ContentManagement logic but filtered
    // For this phase, navigating to general Content Management is sufficient
    // as it handles all types including Announcements.
    ContentManagementScreen(
        onBackClick = onBackClick,
        onEditContent = onEditAnnouncement
    )
}
