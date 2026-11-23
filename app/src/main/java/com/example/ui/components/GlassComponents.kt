package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.GsysCyan
import com.example.ui.theme.GsysDeepNavy
import com.example.ui.theme.GsysError
import com.example.ui.theme.GsysGlassBorderBright
import com.example.ui.theme.GsysGlassBorderCyan
import com.example.ui.theme.GsysGlassBorderSubtle
import com.example.ui.theme.GsysGlassFillBottom
import com.example.ui.theme.GsysGlassFillTop
import com.example.ui.theme.GsysIndigo
import com.example.ui.theme.GsysMidnightSurface
import com.example.ui.theme.GsysTeal
import com.example.ui.theme.GsysTextMuted
import com.example.ui.theme.GsysTextPrimary
import com.example.ui.theme.GsysTextSecondary

@Composable
fun GsysBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GsysDeepNavy)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_gsys_mesh_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.72f,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GsysCyan.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.18f, size.height * 0.14f),
                            radius = size.minDimension * 0.65f
                        ),
                        radius = size.minDimension * 0.65f,
                        center = Offset(size.width * 0.18f, size.height * 0.14f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GsysIndigo.copy(alpha = 0.20f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.88f, size.height * 0.82f),
                            radius = size.minDimension * 0.70f
                        ),
                        radius = size.minDimension * 0.70f,
                        center = Offset(size.width * 0.88f, size.height * 0.82f)
                    )
                }
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GsysDeepNavy.copy(alpha = 0.35f),
                            GsysMidnightSurface.copy(alpha = 0.52f),
                            GsysDeepNavy.copy(alpha = 0.78f)
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    highlightBorder: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val glassBrush = Brush.linearGradient(
        colors = listOf(
            GsysGlassFillTop,
            GsysGlassFillBottom,
            Color.White.copy(alpha = 0.06f)
        )
    )
    val borderBrush = Brush.linearGradient(
        colors = if (highlightBorder) {
            listOf(
                GsysGlassBorderBright,
                GsysGlassBorderCyan,
                GsysGlassBorderSubtle
            )
        } else {
            listOf(
                GsysGlassBorderSubtle,
                GsysGlassBorderSubtle
            )
        }
    )

    Column(
        modifier = modifier
            .clip(shape)
            .background(brush = glassBrush, shape = shape)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GlassSegmentedSwitch(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tagPrefix: String = "segment"
) {
    val containerShape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.dp, GsysGlassBorderSubtle, containerShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val bgColor by animateColorAsState(
                targetValue = if (selected) GsysCyan.copy(alpha = 0.24f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "segmentBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (selected) GsysCyan.copy(alpha = 0.55f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "segmentBorder"
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) GsysTextPrimary else GsysTextMuted,
                label = "segmentText"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    .clickable { onSelect(index) }
                    .testTag("${tagPrefix}_$index")
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = GsysTextMuted.copy(alpha = 0.7f)) }
            } else null,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = label,
                    tint = if (errorText != null) GsysError else GsysCyan
                )
            },
            trailingIcon = trailingContent,
            isError = errorText != null,
            singleLine = singleLine,
            maxLines = maxLines,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                errorContainerColor = GsysError.copy(alpha = 0.08f),
                focusedBorderColor = GsysCyan,
                unfocusedBorderColor = GsysGlassBorderSubtle,
                errorBorderColor = GsysError,
                focusedLabelColor = GsysCyan,
                unfocusedLabelColor = GsysTextSecondary,
                focusedTextColor = GsysTextPrimary,
                unfocusedTextColor = GsysTextPrimary,
                cursorColor = GsysCyan
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (!errorText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall,
                color = GsysError,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
fun GlassPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(16.dp)
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = GsysDeepNavy
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) {
                        listOf(GsysCyan, GsysTeal)
                    } else {
                        listOf(GsysCyan.copy(alpha = 0.35f), GsysTeal.copy(alpha = 0.35f))
                    }
                ),
                shape = shape
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = GsysDeepNavy,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GsysDeepNavy,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = GsysDeepNavy
            )
        }
    }
}

@Composable
fun GlassChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    val elevation by animateDpAsState(if (selected) 4.dp else 0.dp, label = "chipElevation")
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) GsysCyan.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) GsysCyan else GsysGlassBorderSubtle
        ),
        tonalElevation = elevation,
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (selected) GsysCyan else GsysTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) GsysTextPrimary else GsysTextSecondary
            )
        }
    }
}

@Composable
fun GsysBrandHeader(
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                GsysCyan.copy(alpha = 0.30f),
                                GsysIndigo.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .border(1.dp, GsysGlassBorderBright, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "G",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = GsysCyan
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "GSYS PORTAL",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GsysTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GsysTextSecondary
                )
            }
        }
        Surface(
            shape = CircleShape,
            color = GsysTeal.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, GsysTeal.copy(alpha = 0.45f))
        ) {
            Text(
                text = "v2.0",
                style = MaterialTheme.typography.labelMedium,
                color = GsysTeal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}
