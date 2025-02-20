import { ProgressCircular } from "react-onsenui";
import AntiFeatureListItem from "@Components/AntiFeatureListItem";
import { Anchor } from "@Components/dapi/Anchor";
import { Page } from "@Components/onsenui/Page";
import { Toolbar } from "@Components/onsenui/Toolbar";
import { useActivity } from "@Hooks/useActivity";
import { useBlacklist } from "@Hooks/useBlacklist";
import { useStrings } from "@Hooks/useStrings";
import { ExpandLess, ExpandMore } from "@mui/icons-material";
import { Card, CardContent, Collapse, List, ListItem, ListItemIcon, ListItemText, Typography } from "@mui/material";
import FlatList from "flatlist-react";
import React from "react";

interface BlacklistItemProps {
  module: any;
}

function BlacklistItem({ module }: BlacklistItemProps) {
  const [open, setOpen] = React.useState(false);

  const handleClick = () => {
    setOpen((prev) => !prev);
  };

  return (
    <>
      <ListItem>
        <ListItemIcon onClick={handleClick}>{open ? <ExpandLess /> : <ExpandMore />}</ListItemIcon>

        <Anchor color="text.primary" href={module.source} noIcon>
          <ListItemText primary={module.id} secondary={module.source} />
        </Anchor>
      </ListItem>
      <Collapse in={open} timeout="auto" unmountOnExit>
        <List disablePadding>
          {module.notes && (
            <ListItem>
              <Card>
                <CardContent>
                  <Typography sx={{ fontSize: 14 }} color="text.secondary" gutterBottom>
                    Additional notes
                  </Typography>
                  <Typography variant="body2">{module.notes}</Typography>
                </CardContent>
              </Card>
            </ListItem>
          )}
          {typeof module.antifeatures === "string" ? (
            <AntiFeatureListItem sx={{ pl: 4 }} type={module.antifeatures} />
          ) : (
            Array.isArray(module.antifeatures) && module.antifeatures.map((anti) => <AntiFeatureListItem sx={{ pl: 4 }} type={anti} />)
          )}
        </List>
      </Collapse>
    </>
  );
}

function ViewBlacklistedModulesActivity() {
  const { context } = useActivity();
  const { strings } = useStrings();

  const blacklistedModules = useBlacklist();

  const renderToolbar = () => {
    return (
      <Toolbar modifier="noshadow">
        <Toolbar.Left>
          <Toolbar.BackButton onClick={context.popPage} />
        </Toolbar.Left>
        <Toolbar.Center>{strings("blacklisted_modules")}</Toolbar.Center>
      </Toolbar>
    );
  };

  if (blacklistedModules.length === 0) {
    return (
      <Page renderToolbar={renderToolbar}>
        <ProgressCircular
          indeterminate
          style={{
            position: "absolute",
            left: "50%",
            top: "50%",
            WebkitTransform: "translate(-50%, -50%)",
            transform: "translate(-50%, -50%)",
          }}
        />
      </Page>
    );
  }

  return (
    <Page renderToolbar={renderToolbar}>
      <Page.RelativeContent zeroMargin>
        <List>
          <FlatList
            list={blacklistedModules}
            renderItem={(mod) => <BlacklistItem module={mod} />}
            renderOnScroll
            renderWhenEmpty={() => <></>}
          />
        </List>
      </Page.RelativeContent>
    </Page>
  );
}

export default ViewBlacklistedModulesActivity;
