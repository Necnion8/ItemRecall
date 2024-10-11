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
- MythicMobs (v5.6.2で確認)

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
#     対応しているタイプは mythicmobs のみです。
#   置換する新しいアイテムを指定する new 項目を省略することで削除のみ実行できます。
items:
  - old: "mythicmobs:OldItemTypeName" # 回収対象のアイテム
    new: "mythicmobs:NewItemTypeName"  # 置換する新しいアイテム (省略可)
  - "mythicmobs:OldItemTypeName2"  # 回収対象のアイテム。削除のみ (短縮)
```

#### アイテム設定例
```yml
items:
  # MythicMobs DiamondKingSword を MythicMobs IronKingSword に置換する
  - old: "mythicmobs:DiamondKingSword"
    new: "mythicmobs:IronKingSword"
    
  # MythicMobs DiamondKingHelmet を削除する (置き換えない)
  - old: "mythicmobs:DiamondKingHelmet"
```
(注) 置換後のアイテム(new)が指定されているにも関わらず、存在しないアイテムまたは作成できない場合は置換元のアイテムを削除しません。
