package com.bear.asset.ui.navigation

// 仅展示关键修改：去掉中间“添加”文字

@Composable
private fun CenterAddButton(onAddAsset: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(62.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onAddAsset)
            .padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(BrandBlue, BrandPurple))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "添加资产",
                modifier = Modifier.size(27.dp),
                tint = Color.White
            )
        }
    }
}
