package com.alchemist;

import java.awt.Color;
import java.util.Collection;
import java.util.EnumSet;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

public class MockRole implements Role {

	@Override
	public String getAsMention() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getIdLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EnumSet<Permission> getPermissions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumSet<Permission> getPermissionsExplicit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPermission(Permission... permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPermission(Collection<Permission> permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int compareTo(Role o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPositionRaw() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isManaged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHoisted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMentionable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getPermissionsRaw() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColorRaw() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPublicRole() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canInteract(Role role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Guild getGuild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RoleAction createCopy(Guild guild) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RoleManager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuditableRestAction<Void> delete() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JDA getJDA() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumSet<Permission> getPermissions(net.dv8tion.jda.api.entities.channel.middleman.GuildChannel channel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumSet<Permission> getPermissionsExplicit(
			net.dv8tion.jda.api.entities.channel.middleman.GuildChannel channel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPermission(net.dv8tion.jda.api.entities.channel.middleman.GuildChannel channel,
			Permission... permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPermission(net.dv8tion.jda.api.entities.channel.middleman.GuildChannel channel,
			Collection<Permission> permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSync(IPermissionContainer targetChannel, IPermissionContainer syncSource) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSync(IPermissionContainer channel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RoleTags getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RoleIcon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
