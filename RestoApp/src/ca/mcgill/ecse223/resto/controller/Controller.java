package ca.mcgill.ecse223.resto.controller;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.view.TableView;

public class Controller {
	private RestoApp service;

	/**
	 * constructor for the controller
	 * 
	 * @param service
	 *            the model system
	 */
	public Controller(RestoApp service) {
		super();
		this.service = service;
	}

	/**
	 * This method adds a new table to the system
	 * 
	 * @param number:
	 *            table number
	 * @param x:
	 *            table location
	 * @param y:
	 *            table location
	 * @param width:
	 *            table width
	 * @param length:
	 *            table length
	 * @param numberOfSeats:
	 *            number of seats on that table
	 * @return a new table view
	 * 
	 * @throws InvalidInputException
	 */
	public TableView addTable(int number, int x, int y, int width, int length, int numberOfSeats)
			throws InvalidInputException {
		// check input
		if (number < 0 || x < 0 || y < 0 || width <= 0 || length <= 0 || numberOfSeats < 0) {
			throw new InvalidInputException("one of the inputs is zero or negative");
		}

		// check if another table has the same number
		for (Table t : service.getTables()) {
			if (t.getNumber() == number)
				throw new InvalidInputException("the table number already exsits");
		}

		Rectangle rect = new Rectangle(x, y, width, length);

		// check if the table clash
		for (Table t : service.getTables()) {
			if (tableOverLap(tableToRectangle(t), rect)) {
				throw new InvalidInputException("the table overlaps with another table");
			}
		}

		//passwed all checks
		Table newt = new Table(number, x, y, width, length, service);
		for (int i = 0; i < numberOfSeats; i++) {
			newt.addSeat();
		}

		// saving
		RestoApplication.save();

		return convertToViewObject(newt);
	}

	/**
	 * Give a list of all tables views
	 * @return list of all table views
	 */
	public List<TableView> getAllTables() {
		List<TableView> tvs = new ArrayList<TableView>();
		for (Table t : service.getTables()) {
			System.out.println(t.getNumber());
			tvs.add(convertToViewObject(t));
		}
		return tvs;
	}

	/**
	 * Changing a table location
	 * @param number: number of the table
	 * @param newX: new coordinates
	 * @param newY: new coordiantes
	 * @throws InvalidInputException thrown when arguments are wrong or on overlap
	 */
	public void changeTableLocation(int number, int newX, int newY) throws InvalidInputException {

		if (newX < 0 || newY < 0)
			throw new InvalidInputException("arguments are wrong");
		
		Table found = null;
		for (Table t : service.getTables()) {
			if (t.getNumber() == number) {
				found = t;
				break;
			}
		}
		
		if (found == null)
			throw new InvalidInputException("table was not found");
		
		//dimensions with the new location
		Rectangle rect = new Rectangle(newX, newY, found.getX(), found.getY());
		
		//check for collision
		for(Table t: service.getTables()) {
			if(t == found)
				continue;
			if(tableOverLap(rect, tableToRectangle(t)))
					throw new InvalidInputException("table will collide with table number "+ t.getNumber());
		}
		
		//all good set new coordinates
		found.setX(newX);
		found.setY(newY);
		
		// saving
		RestoApplication.save();
		
	}
	
	/**
	 * returns a list of all table numbers
	 * @return an array of Integers
	 */
	public Integer[] getAllTableNumbers() {
		Integer[] a = new Integer[service.getTables().size()];
		for(int i=0; i<a.length; i++) {
			a[i] = service.getTable(i).getNumber();
		}
		return a;
	}
	
	/** CONVERSION METHODS **/

	TableView convertToViewObject(Table t) {
		return new TableView(t);
	}

	/** HELPER METHODS **/

	/**
	 * This method checks if two line segments specified by the starting point and
	 * length do intersect or not
	 * 
	 * @param first:
	 *            coordinate of the first point
	 * @param firstWidth:
	 *            the length of the first line segement
	 * @param second:
	 *            coordinate of the second point
	 * @param secondWidth:
	 *            coordinate of the second line segement
	 * @return boolean: overlap or not
	 */
	private boolean linearIntersection(double first, double firstWidth, double second, double secondWidth) {
		// make sure first is smaller
		if (first > second) {
			double tmp = first;
			first = second;
			second = tmp;

			tmp = firstWidth;
			firstWidth = secondWidth;
			secondWidth = tmp;
		}

		if (first + firstWidth > second)
			return true;

		return false;
	}

	/**
	 * This method checks if two tables overlap or not
	 * 
	 * @param rect1
	 *            the first rectangle
	 * @param rect2
	 *            the second rectangle
	 * @return boolean: overlap or not
	 */
	private boolean tableOverLap(Rectangle rect1, Rectangle rect2) {
		return linearIntersection(rect1.getX(), rect1.getWidth(), rect2.getX(), rect2.getWidth())
				&& linearIntersection(rect1.getY(), rect1.getHeight(), rect2.getY(), rect2.getHeight());
	}

	private Rectangle tableToRectangle(Table t) {
		return new Rectangle(t.getX(), t.getY(), t.getWidth(), t.getLength());
	}
}