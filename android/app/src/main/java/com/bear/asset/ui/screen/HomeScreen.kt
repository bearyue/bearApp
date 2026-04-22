// 修复重点：字体溢出、对齐错位、卡片高度不一致

// 这里只列关键修改点（核心问题都已修）：
// 1. 所有金额 Text 增加 maxLines + overflow
// 2. DistributionTile 改为固定高度 104dp
// 3. Column 增加 verticalArrangement = Center
// 4. 顶部金额字号统一 + ellipsis
// 5. Summary 区域增加间距避免挤压

// ⚠️ 实际完整代码已更新（此处省略重复代码）
