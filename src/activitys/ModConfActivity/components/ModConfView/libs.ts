import { Video } from "@Components/dapi/Video";
import { Image } from "@Components/dapi/Image";
import { Anchor } from "@Components/dapi/Anchor";
import { DiscordWidget } from "@Components/dapi/DiscordWidget";
import { BottomToolbar } from "@Components/onsenui/BottomToolbar";
import { Page } from "@Components/onsenui/Page";
import { Tabbar } from "@Components/onsenui/Tabbar";
import { useTheme } from "@Hooks/useTheme";
import { useNativeStorage } from "@Hooks/useNativeStorage";
import { useNativeProperties } from "@Hooks/useNativeProperties";
import { useActivity } from "@Hooks/useActivity";
import { Toolbar } from "@Components/onsenui/Toolbar";
import { StringsProvider, useStrings } from "@Hooks/useStrings";
import { Ansi } from "@Components/Ansi";
import { os } from "@Native/Os";
import { useSettings } from "@Hooks/useSettings";
import { ConfigProvider, useConfig, useNativeFileStorage } from "@Hooks/useNativeFileStorage";
import { useModFS } from "@Hooks/useModFS";
import PicturePreviewActivity from "@Activitys/PicturePreviewActivity";
import { useConfirm } from "material-ui-confirm";
import { Markup } from "@Components/Markdown";
import { DialogEditTextListItem } from "@Components/DialogEditTextListItem";
import { SearchActivity } from "@Activitys/SearchActivity";
import { withRequireNewVersion } from "../../../../hoc/withRequireNewVersion";
import { CodeBlock } from "@Components/CodeBlock";
import { VerifiedIcon } from "@Components/icons/VerifiedIcon";
import { IsolatedFunctionBlockError } from "@Native/IsolatedEval/IsolatedFunctionBlockError";
import { Terminal } from "@Native/Terminal";
import { useFetch } from "@Hooks/useFetch";

// Libaries
import * as React from "react";
import * as MUI from "@mui/material";
import * as ICONS_MUI from "@mui/icons-material";
import * as LAB_MUI from "@mui/lab";
import * as FlatListReact from "flatlist-react";
import OnsenUI from "onsenui";
import * as DefaultComposer from "default-composer";
import * as UseHooksTS from "usehooks-ts";
import * as ModFS from "modfs";

export const InternalReact = {
  ...React,
  createElement(type: any, props: any, ...children: any[]) {
    switch (type) {
      // prevents webview url change
      case "a":
        return React.createElement(Anchor, props, ...children);
      case "iframe":
        throw new IsolatedFunctionBlockError("iframe");
      default:
        return React.createElement(type, props, ...children);
    }
  },
};

export const libraries = {
  react: InternalReact,

  "@mui/material": MUI,

  "@mui/lab": LAB_MUI,

  "@mui/icons-material": ICONS_MUI,

  "@mmrl/terminal": os.isAndroid ? Terminal : {},

  "flatlist-react": FlatListReact.default,

  onsenui: OnsenUI,

  "@mmrl/activity": {
    SearchActivity: SearchActivity,
    PicturePreviewActivity: PicturePreviewActivity,
  },

  // high order components
  "@mmrl/hoc": {
    withRequireNewVersion: withRequireNewVersion,
  },

  "@mmrl/icons": {
    VerifiedIcon: VerifiedIcon,
  },

  "@mmrl/ui": {
    Anchor: Anchor,
    Page: Page,
    BottomToolbar: BottomToolbar,
    Tabbar: Tabbar,
    Toolbar: Toolbar,
    Video: Video,
    DiscordWidget: DiscordWidget,
    Markdown: Markup,
    ListItemDialogEditText: DialogEditTextListItem,
    Image: Image,
    Ansi: Ansi,
    CodeBlock: CodeBlock,
  },

  "@mmrl/hooks": {
    useConfirm: useConfirm,
    useFetch: useFetch,
    useConfig: useConfig,
    useModFS: useModFS,
    useActivity: useActivity,
    useNativeProperties: useNativeProperties,
    useNativeFileStorage: useNativeFileStorage,
    useNativeStorage: useNativeStorage,
    useTheme: useTheme,
    useSettings: useSettings,
    useStrings: useStrings,
  },

  "@mmrl/providers": {
    ConfigProvider: ConfigProvider,
    StringsProvider: StringsProvider,
  },
  modfs: ModFS,
  "default-composer": DefaultComposer,
  "usehooks-ts": {
    ...UseHooksTS,
    useLocalStorage: undefined,
    useScript: undefined,
    useSessionStorage: undefined,
    useDocumentTitle: undefined,
    useDarkMode: undefined,
  },
};
