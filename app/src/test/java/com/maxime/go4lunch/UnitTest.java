package com.maxime.go4lunch;

import android.net.Uri;

import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Restaurant;
import com.maxime.go4lunch.model.Workmate;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertSame;


public class UnitTest {

    Workmate workmate1;
    Workmate workmate2;
    Workmate workmate3;
    Workmate workmate4;
    Restaurant restaurant1;
    Restaurant restaurant2;
    Restaurant restaurant3;
    Like like1;
    Like like2;
    Like like3;
    Like like4;

    @Test
    public void testRestaurantForWorkmate() {
        getValueForWorkmate();
        getValueForRestaurant();
        restaurant1.getWorkmatesEatingHere().add(workmate1);
        restaurant1.getWorkmatesEatingHere().add(workmate2);
        restaurant2.getWorkmatesEatingHere().add(workmate3);
        restaurant3.getWorkmatesEatingHere().add(workmate4);
        assert(restaurant1.getWorkmatesEatingHere().size() == 2);
        assert(restaurant2.getWorkmatesEatingHere().size() == 1);
        assert(restaurant3.getWorkmatesEatingHere().size() == 1);
        assert(restaurant1.getWorkmatesEatingHere().contains(workmate1));
        assert(restaurant1.getWorkmatesEatingHere().contains(workmate2));
        assert(restaurant2.getWorkmatesEatingHere().contains(workmate3));
        assert(restaurant3.getWorkmatesEatingHere().contains(workmate4));
    }

    @Test
    public void testWorkmateChoiceRestaurant () {
        getValueForWorkmate();
        getValueForRestaurant();
        workmate1.setRestaurant(restaurant1.getName());
        workmate2.setRestaurant(restaurant1.getName());
        workmate3.setRestaurant(restaurant2.getName());
        workmate4.setRestaurant(restaurant3.getName());
        assert(workmate1.getRestaurant().equals(restaurant1.getName()));
        assert(workmate2.getRestaurant().equals(restaurant1.getName()));
        assert(workmate3.getRestaurant().equals(restaurant2.getName()));
        assert(workmate4.getRestaurant().equals(restaurant3.getName()));
    }

    @Test
    public void testLikeByWorkmateForRestaurant() {
        getValueForWorkmate();
        getValueForRestaurant();
        getValueForLike();
        like1.setStarNumber(1);
        like2.setStarNumber(3);
        like3.setStarNumber(2);
        like4.setStarNumber(1);
        restaurant1.getStarNumberForRestaurant();
        restaurant2.getStarNumberForRestaurant();
        restaurant3.getStarNumberForRestaurant();
        assert(restaurant1.getStar() == 2);
        assert(restaurant2.getStar() == 2);
        assert(restaurant3.getStar() == 1);

    }

    @Test
    public void testWorkmateComparatorForRestaurant() {
        getValueForRestaurant();
        restaurant1.getWorkmatesEatingHere().add(workmate1);
        restaurant1.getWorkmatesEatingHere().add(workmate2);
        restaurant2.getWorkmatesEatingHere().add(workmate3);
        restaurant3.getWorkmatesEatingHere().add(workmate4);
        final ArrayList<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(restaurant2);
        restaurants.add(restaurant1);
        restaurants.add(restaurant3);
        Collections.sort(restaurants, new Restaurant.RestaurantNumberOfWorkmatesComparator());

        assertSame(restaurants.get(0), restaurant1);
        assertSame(restaurants.get(1), restaurant2);
        assertSame(restaurants.get(2), restaurant3);
    }

    @Test
    public void testLikeComparatorForRestaurant() {
        getValueForWorkmate();
        getValueForRestaurant();
        getValueForLike();
        final ArrayList<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(restaurant2);
        restaurants.add(restaurant1);
        restaurants.add(restaurant3);
        like1.setStarNumber(3);
        like2.setStarNumber(3);
        like3.setStarNumber(2);
        like4.setStarNumber(1);
        restaurant1.getStarNumberForRestaurant();
        restaurant2.getStarNumberForRestaurant();
        restaurant3.getStarNumberForRestaurant();
        Collections.sort(restaurants, new Restaurant.RestaurantStarsComparator());

        assertSame(restaurants.get(0), restaurant1);
        assertSame(restaurants.get(1), restaurant2);
        assertSame(restaurants.get(2), restaurant3);
    }

    public void getValueForWorkmate() {
        workmate1 = new Workmate ("1", "Avatar1", "Jean");
        workmate2 = new Workmate ("2", "Avatar2", "Jacques");
        workmate3 = new Workmate ("3", "Avatar3", "Lucie");
        workmate4 = new Workmate ("4", "Avatar4", "Léa");
    }

    public void getValueForRestaurant() {
        Uri website = null;
        restaurant1 = new Restaurant ("58", "AvatarRestaurant1", "Quick", "?", "?", "?", website, 0);
        restaurant2 = new Restaurant ("76", "AvatarRestaurant2", "McDo", "?", "?", "?", website, 0);
        restaurant3 = new Restaurant ("92", "AvatarRestaurant3", "BurgerKing", "?", "?", "?", website, 0);
    }

    public void getValueForLike() {
        like1 = new Like (workmate1.getId()+restaurant1.getId(), workmate1.getId(), restaurant1.getId());
        restaurant1.getLikes().add(like1);
        like2 = new Like (workmate2.getId()+restaurant1.getId(), workmate2.getId(), restaurant1.getId());
        restaurant1.getLikes().add(like2);
        like3 = new Like (workmate3.getId()+restaurant2.getId(), workmate3.getId(), restaurant2.getId());
        restaurant2.getLikes().add(like3);
        like4 = new Like (workmate4.getId()+restaurant3.getId(), workmate4.getId(), restaurant3.getId());
        restaurant3.getLikes().add(like4);
    }









}