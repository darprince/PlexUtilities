package com.dprince.plex.tv.api.thetvdb.types;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_ShowFolderData.Builder.class)
@NonNullByDefault
public abstract class ShowFolderData {
public abstract ShowIdData = getShowIdData();

public abstract List<SeasonData> = getSeasonData();

}
