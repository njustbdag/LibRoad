package njust.lib.dao;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.njust.otherUtils.HibernateUtil;

public class BaseDAO <T>{
	
	private Session session = null;
	private Transaction tran = null;
	private static final int BATCH_SIZE = 1000;
	private static SessionFactory sessionFactory = null;
	static{
		try{
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据id得到实体
	 * @param entityClazz 实体类型
	 * @param id 实体id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T findByID(Class<T> entityClazz, Serializable id) {
		
		T object = null;
		try {
			session = HibernateUtil.getSession();
			object = (T) session.get(entityClazz, id);
		} catch (Exception e) {
		} finally {
			if (session != null) {
				// 关闭session
				session.close();
			}
		}
		return object;
	}
	
	/**
	 * 新增实体
	 * @param entity 对象
	 * @return
	 */
	public boolean add(T entity) {
		boolean result = false;
		try {
			session = HibernateUtil.getSession();
			tran = session.beginTransaction();
			session.save(entity);
			tran.commit();
			result = true;
		} catch (Exception e) {
			if (tran != null) {
				// 事物回滚
				tran.rollback();
			}
		} finally {
			if (session != null) {
				// 关闭session
				session.close();
			}
		}
		return result;
	}
	/**
	 * 新增实体
	 * @param entities List<T>
	 * @return
	 */
	public boolean add(List<T> entities) {
		boolean result = false;
		try {
			session = HibernateUtil.getSession();
			tran = session.beginTransaction();
			for(int i = 0,len = entities.size();i < len;i ++){
				session.save(entities.get(i));
				if(i % BATCH_SIZE == 0){
					session.flush();
					session.clear();
				}
			}
			tran.commit();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (tran != null) {
				// 事物回滚
				tran.rollback();
			}
		} finally {
			if (session != null) {
				// 关闭session
				session.close();
			}
		}
		return result;
	}

	/***
	 * 更新实体
	 * @param entity 实体对象
	 * @return
	 */
	public boolean update(T entity) {
		boolean result = false;
		try {
			session = HibernateUtil.getSession();
			tran = session.beginTransaction();
			session.update(entity);
			tran.commit();
			result = true;
		} catch (Exception e) {
			if (tran != null) {
				// 事物回滚
				tran.rollback();
			}
		} finally {
			if (session != null) {
				// 关闭session
				session.close();
			}
		}
		return result;
	}
	
	/***
	 * 删除实体
	 * @param entity 实体对象
	 * @return
	 */
	public boolean delete(T entity) {
		boolean result = false;
		try {
			session = HibernateUtil.getSession();
			tran = session.beginTransaction();
			session.delete(entity);
			tran.commit();
			result = true;
		} catch (Exception e) {
			if (tran != null) {
				// 事物回滚
				tran.rollback();
			}
		} finally {
			if (session != null) {
				// 关闭session
				session.close();
			}
		}
		return result;
	}
	
	/**
	 * 根据id删除实体
	 * @param entityClazz 实体类型
	 * @param id 实体id
	 * @return
	 */
	public boolean deleteByID(Class<T> entityClazz, Serializable id) {
		boolean result = false;
		try {
			session = HibernateUtil.getSession();
			tran = session.beginTransaction();
			session.createQuery("delete " + entityClazz.getSimpleName()
					+ " en where en.id = ?0")
					.setParameter("0" , id);
			tran.commit();
			result = true;
		} catch (Exception e) {
			if (tran != null) {
				// 事物回滚
				tran.rollback();
			}
		} finally {
			if (session != null) {
				// 关闭session
				session.close();
			}
		}
		return result;
	}

	/**
	 * 获取所有实体
	 * @param entityClazz 实体类型
	 * @return
	 */
	public List<T> findAll(Class<T> entityClazz)
	{
		return findByHql("select en from "
			+ entityClazz.getSimpleName() + " en");
	}
		
	/**
	 * 根据HQL语句查询实体
	 * @param hql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByHql(String hql) {

		List<T> list = new ArrayList<T>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			list = (List<T>)query.list();
		} catch (Exception e) {
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
	
	/**
	 * 根据带占位符参数的HQL语句查询实体
	 * @param hql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByHql(String hql, Object... params) {

		List<T> list = new ArrayList<T>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			for(int i = 0 , len = params.length ; i < len ; i++) {
				query.setParameter(i + "" , params[i]);
			}
			list = (List<T>)query.list();
		} catch (Exception e) {
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}

	
	/**
	 * 根据HQL语句查询单个实体
	 * @param hql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  T findOneByHql(String hql) {

		List<T> list = new ArrayList<T>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			list =  (List<T>)query.list();
			if(list.size()>0)
				return list.get(0);
		} catch (Exception e) {
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}
	
	/**
	 * 根据带占位符参数的HQL语句查询单个实体
	 * @param hql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T findOneByHql(String hql, Object... params) {

		List<T> list = new ArrayList<T>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			for(int i = 0 , len = params.length ; i < len ; i++) {
				query.setParameter(i + "" , params[i]);
			}
			list = (List<T>)query.list();
			if(list.size()>0)
				return list.get(0);
		} catch (Exception e) {
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;
	}

	/**
	 * 使用HQL语句进行分页查询操作
	 * @param hql
	 * @param pageNow 当前页数
	 * @param pageSize 每页记录数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByPage(String hql,  int pageNow, int pageSize) {
		List<T> list = new ArrayList<T>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			// 筛选条数
			query.setFirstResult((pageNow - 1) * pageSize);
			query.setMaxResults(pageSize);
			list =  (List<T>)query.list();
		} catch (Exception e) {
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
	
	/**
	 * 使用带占位符的HQL语句进行分页查询操作
	 * @param hql
	 * @param pageNow 当前页数
	 * @param pageSize 每页记录数
	 * @param params 占位符参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByPage(String hql,  int pageNow, int pageSize, Object... params) {
		List<T> list = new ArrayList<T>();
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			for(int i = 0 , len = params.length ; i < len ; i++) {
				query.setParameter(i + "" , params[i]);
			}
			// 筛选条数
			query.setFirstResult((pageNow - 1) * pageSize);
			query.setMaxResults(pageSize);
			list =  (List<T>)query.list();
		} catch (Exception e) {
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return list;
	}
	

	/**
	 * 根据HQL语句查询记录条数
	 * @param hql
	 * @return
	 */
	public int findCount(String hql) {
		int result = 0;
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			result = Integer.valueOf(query.iterate().next().toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return result;
	}
	
	/**
	 * 根据HQL语句查询记录条数
	 * @param hql
	 * @return
	 */
	public int findCount(String hql, Object... params) {
		int result = 0;
		try {
			session = HibernateUtil.getSession();
			Query query = session.createQuery(hql);
			for(int i = 0 , len = params.length ; i < len ; i++) {
				query.setParameter(i + "" , params[i]);
			}
			result = Integer.valueOf(query.iterate().next().toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return result;
	}

}