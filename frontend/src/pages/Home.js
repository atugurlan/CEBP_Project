import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { toast } from "../hooks/use-toast";
import { Button } from "../components/ui/button";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "../components/ui/form";
import { Input } from "../components/ui/input";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from "../components/ui/card";

const OfferFormSchema = z.object({
  stockType: z.string().nonempty({ message: "Stock type is required." }),
  noOfStocks: z.string().nonempty({ message: "At least 1 stock is required." }),
  pricePerStock: z.string().nonempty({ message: "Price must be greater than 0." }),
  offerType: z.enum(["SELL", "BUY"]),
});

const Home = () => {
  const { user } = useAuth();
  const [stockWallet, setStockWallet] = useState([]);
  const [allStockTypes, setAllStockTypes] = useState([]);
  const [moneyWallet, setMoneyWallet] = useState(0);
  const [showForm, setShowForm] = useState(false);

  // Fetch wallet details
  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const stockResponse = await axios.get(
          `http://localhost:8080/stock-wallets/client/${user.id}`
        );
        setStockWallet(stockResponse.data);

        const moneyResponse = await axios.get(
          `http://localhost:8080/clients/${user.id}/money-wallet`
        );
        setMoneyWallet(moneyResponse.data.moneyWallet);

        const stockTypesResponse = await axios.get("http://localhost:8080/stock-types");
        setAllStockTypes(stockTypesResponse.data);
      } catch (err) {
        toast({
          title: "Error",
          description: "Failed to fetch wallet or stock type details.",
          variant: "destructive",
        });
      }
    };

    fetchDetails();
  }, [user.id]);

  const form = useForm({
    resolver: zodResolver(OfferFormSchema),
    defaultValues: {
      stockType: "",
      noOfStocks: "",
      pricePerStock: "",
      offerType: "SELL",
    },
  });

  const handleSubmit = async (data) => {
    try {
      const offerPayload = {
        client: user.id,
        stockType: data.stockType,
        noOfStocks: parseInt(data.noOfStocks, 10),
        pricePerStock: parseFloat(data.pricePerStock),
        offerType: data.offerType,
      };

      await axios.post("http://localhost:8080/offers", offerPayload);

      toast({
        title: "Success",
        description: "Offer successfully added!",
      });

      form.reset();
      setShowForm(false);
    } catch (err) {
      toast({
        title: "Error",
        description: "Failed to add offer. Please try again.",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      {/* Welcome Section */}
      <header className="text-center mb-8">
        <h1 className="text-5xl font-bold font-sans text-gray-800 mb-4">
          Welcome to the Home Page
        </h1>
        <p className="text-lg text-gray-600">This is your main dashboard.</p>
      </header>

      {/* Add Offer Button */}
      <Button onClick={() => setShowForm(!showForm)} className="mb-6">
        {showForm ? "Cancel" : "Add an Offer"}
      </Button>

      {/* Offer Form */}
      {showForm && (
        <Card className="w-full max-w-xl">
          <CardHeader>
            <CardTitle>Add a New Offer</CardTitle>
            <CardDescription>Fill in the details to create a new offer.</CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
                {/* Offer Type */}
                <FormField
                  control={form.control}
                  name="offerType"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Offer Type</FormLabel>
                      <FormControl>
                        <select
                          {...field}
                          onChange={(e) => {
                            form.setValue("stockType", ""); // Reset stockType on change
                            field.onChange(e);
                          }}
                          className="border rounded-md p-2 w-full"
                        >
                          <option value="SELL">SELL</option>
                          <option value="BUY">BUY</option>
                        </select>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Stock Type */}
                <FormField
                  control={form.control}
                  name="stockType"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Stock Type</FormLabel>
                      <FormControl>
                        <select
                          {...field}
                          className="border rounded-md p-2 w-full"
                        >
                          <option value="">Select a stock type</option>
                          {form.getValues("offerType") === "SELL"
                            ? stockWallet.map((stock, index) => (
                                <option key={index} value={stock.stockType}>
                                  {stock.stockType} (Available: {stock.quantity})
                                </option>
                              ))
                            : allStockTypes.map((type, index) => (
                                <option key={index} value={type}>
                                  {type}
                                </option>
                              ))}
                        </select>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Number of Stocks */}
                <FormField
                  control={form.control}
                  name="noOfStocks"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Number of Stocks</FormLabel>
                      <FormControl>
                        <Input type="number" placeholder="Enter the number of stocks" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {/* Price Per Stock */}
                <FormField
                  control={form.control}
                  name="pricePerStock"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Price Per Stock</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          step="0.01"
                          placeholder="Enter the price per stock"
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <Button type="submit" className="w-full">
                  Submit Offer
                </Button>
              </form>
            </Form>
          </CardContent>
          <CardFooter>
            <p className="text-muted-foreground text-sm">
              Ensure all details are correct before submitting.
            </p>
          </CardFooter>
        </Card>
      )}
    </div>
  );
};

export default Home;