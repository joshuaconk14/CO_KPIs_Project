# ConklinOfficial KPI Dashboard

## Overview

The ConklinOfficial KPI Dashboard is an interactive web application built using Streamlit, designed to analyze the relationship between Key Performance Indicators (KPIs) and content shared on social media platforms. This dashboard provides insights into various metrics such as likes, shares, saves, and more, helping users understand the performance of their content over time.

## Features

- **Data Visualization**: Interactive charts displaying KPIs over time, allowing users to visualize trends and performance.
- **Dynamic KPI Selection**: Users can select different KPIs to analyze, including likes, shares, saves, reach, and more.
- **Performance Analysis**: The dashboard highlights good and bad performance metrics using percentile lines, making it easy to identify high and low-performing posts.
- **User-Friendly Interface**: A clean and responsive layout that enhances user experience.

## Technologies Used

- **Python**: The primary programming language for the application.
- **Streamlit**: A powerful framework for building web applications in Python.
- **Pandas**: For data manipulation and analysis.
- **NumPy**: For numerical operations.
- **Plotly**: For creating interactive visualizations.

## Installation

To run this project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/conklinofficial-kpi-dashboard.git
   cd conklinofficial-kpi-dashboard
   ```

2. **Create a virtual environment** (optional but recommended):
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows use `venv\Scripts\activate`
   ```

3. **Install the required packages**:
   ```bash
   pip install -r requirements.txt
   ```

4. **Run the application**:
   ```bash
   streamlit run app.py
   ```

5. **Open your web browser** and navigate to `http://localhost:8501` to view the dashboard.

## Usage

Once the application is running, you can:

- View raw data from the CSV file.
- Analyze likes on posts over the last three months.
- Click on dates to view corresponding Instagram posts.
- Select different KPIs from a dropdown menu to visualize their performance.

## Contributing

Contributions are welcome! If you have suggestions for improvements or new features, please open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to the Streamlit community for their support and resources.
- Special thanks to the contributors who have helped improve this project.

## Contact

For any inquiries, please reach out to [joshua@caffeinated.org](mailto:joshua@caffeinated.org).