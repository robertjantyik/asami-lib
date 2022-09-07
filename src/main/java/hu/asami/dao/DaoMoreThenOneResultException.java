package hu.asami.dao;

public class DaoMoreThenOneResultException extends DaoException{

    public DaoMoreThenOneResultException(String msg) {
        super(msg);
    }
}
