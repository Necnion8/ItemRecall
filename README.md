# ItemRecall
回収対応になったアイテムを削除、または置換するプラグイン<br>
※ このプラグインはプレイヤーインベントリ上のアイテムのみ処理します

#### 実行するタイミングとチェック範囲
- プラグインロード時 -> インベントリ全体
- プレイヤー参加時 -> インベントリ全体
- 拾うor投げたアイテム
- ホットバーのアイテム選択を切り替えた時
- インベントリを閉じた時 (対象アイテムに触れた場合のみ) -> インベントリ全体

## 前提
- Spigot 1.13 以上 (またはその派生)
- 連携プラグイン (オプション)
  - MythicMobs (v5.6.2で確認)
  - MMOItems (v6.9.4で確認)
  - ItemsAdder (v4.0.2-beta-release-11で確認)

## コマンドと権限
- アイテムを処理しない権限
> 権限: `itemrecall.bypass`
<br>

- 管理者用権限 - (bypass権限を含む)
> 権限: `itemrecall.admin` (default: OP)

## 設定
```yml
# デバッグログをコンソールに出力する
enable-debug: false

# 置換するアイテムの設定
#   アイテムの指定
#     "(タイプ):(アイテム)"
#     タイプにはプラグイン名が入り、アイテムにはアイテムを識別するIDや名前を指定します。
#     対応しているタイプは mythicmobs, mmoitems, itemsadder です。
#   置換する新しいアイテムを指定する new 項目を省略することで削除のみ実行できます。
items:
  - old: "mythicmobs:OldItemTypeName" # 回収対象のアイテム
    new: "mythicmobs:NewItemTypeName"  # 置換する新しいアイテム (省略可)
  - "mmoitems:ITEM_TYPE:ITEM_ID"  # 回収対象のアイテム。削除のみ (短縮)
  - "itemsadder:namespace:itemid"
```

#### アイテム設定例
```yml
items:
  # MythicMobs DiamondKingSword を MythicMobs IronKingSword に置換する
  - old: "mythicmobs:DiamondKingSword"
    new: "mythicmobs:IronKingSword"
    
  # MMOItems SWORDタイプ AMETHYST_SWORD を削除する (置き換えない)
  - old: "mmoitems:SWORD:AMETHYST_SWORD"

  # ItemsAdder my_items:mysword を削除する (置き換えない)
  - old: "itemsadder:my_items:mysword"
```
(注) 置換後のアイテム(new)が指定されているにも関わらず、存在しないアイテムまたは作成できない場合は置換元のアイテムを削除しません。

## API
> [ItemRecallEvent](src%2Fmain%2Fjava%2Fcom%2Fgmail%2Fnecnionch%2Fmyplugin%2Fitemrecall%2Fbukkit%2Fevent%2FItemRecallEvent.java) (cancellable)<br>
> プラグインがアイテムを削除または置換する時に実行されるイベント
> ```java
> @EventHandler
> public void onItemRecall(ItemRecallEvent event) {
>   // 元アイテムと置換する新しいアイテムの参照
>   @NotNull ItemStack oldItemStack = event.getOldItemStack();  
>   @Nullable ItemStack newItemStack = event.getNewItemStack();
> 
>   // 置換するアイテムの再設定
>   event.setNewItemStack(newItemStack);
> }
> ```
