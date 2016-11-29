package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.User;

public class UserQuery extends AbstractQuery<User>
{
	public User findByPosLogin(final Integer posLogin)
	{
		Expression expression = new ExpressionBuilder(User.class).get("posLogin").equal(posLogin);
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		return this.find(expression);
	}

	public User findByUsername(final String username)
	{
		Expression expression = new ExpressionBuilder().get("username").equal(username.toLowerCase());
		return this.find(expression);
	}

	public User findByUsernameAndPassword(final String username, final String password)
	{
		Expression expression = new ExpressionBuilder().get("username").equal(username.toLowerCase());
		expression = expression.and(new ExpressionBuilder().get("password").equal(password));
		return this.find(expression);
	}

	public boolean isPosLoginUnique(final Integer posLogin, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("posLogin", posLogin);
		return super.isUniqueValue(params, id);
	}

	public boolean isUsernameUnique(final String username, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		return super.isUniqueValue(params, id);
	}

	public List<User> selectByRole(final Role role)
	{
		final Expression roleExpr = new ExpressionBuilder().get("role").equal(role);
		try
		{
			return this.select(roleExpr);
		}
		catch (Exception e)
		{
			return new ArrayList<User>();
		}
	}

	@Override
	protected Class<User> getEntityClass()
	{
		return User.class;
	}
}
